package com.cMall.feedShop.user.application.service;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;
import com.cMall.feedShop.common.service.EmailService;
import com.cMall.feedShop.user.application.dto.request.UserSignUpRequest;
import com.cMall.feedShop.user.application.dto.response.UserResponse;
import com.cMall.feedShop.user.domain.enums.UserRole;
import com.cMall.feedShop.user.domain.enums.UserStatus;
import com.cMall.feedShop.user.domain.model.User;
import com.cMall.feedShop.user.domain.model.UserProfile;
import com.cMall.feedShop.user.domain.repository.UserProfileRepository;
import com.cMall.feedShop.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import com.cMall.feedShop.user.domain.exception.UserException;
import org.junit.jupiter.api.AfterEach;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupAdminAuthentication() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin@example.com",
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserSignUpRequest signUpRequest;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        signUpRequest = new UserSignUpRequest();
        signUpRequest.setName("테스트유저");
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password123!");
        signUpRequest.setConfirmPassword("password123!");
        signUpRequest.setRole(UserRole.USER);
        signUpRequest.setPhone("01012345678");
    }

    // 회원가입 성공 - 신규 가입 (PENDING 상태, 인증 메일 발송)
    @Test
    @DisplayName("회원가입 성공 - 신규 가입, 인증 메일 발송")
    void signUp_Success_NewUser_Pending() {
        // Given
        given(userRepository.findByEmail(signUpRequest.getEmail())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn("encoded_password");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        // When
        UserResponse response = userService.signUp(signUpRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(signUpRequest.getEmail());
        verify(emailService, times(1)).sendSimpleEmail(
                eq(signUpRequest.getEmail()),
                contains("회원가입을 완료해주세요"),
                contains("인증을 완료해주세요")
        );
    }

    // 회원가입 실패 - 이미 ACTIVE 상태
    @Test
    @DisplayName("회원가입 실패 - 이미 ACTIVE 상태")
    void signUp_Fail_ActiveUser() {
        // Given
        User existingUser = new User();
        existingUser.setStatus(UserStatus.ACTIVE);
        given(userRepository.findByEmail(signUpRequest.getEmail())).willReturn(Optional.of(existingUser));

        // When & Then
        UserException thrown = assertThrows(UserException.class, () ->
                userService.signUp(signUpRequest)
        );
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
        verify(emailService, never()).sendSimpleEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("회원가입 성공 - 비밀번호가 AOP에 의해 이미 암호화된 경우")
    void signUp_Success_AOPEncryptedPassword() {
        // Given
        String preEncryptedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyza.abcdefghijklmnopqrs.";
        signUpRequest.setPassword(preEncryptedPassword);
        signUpRequest.setConfirmPassword(preEncryptedPassword);

        // findByEmail로 중복 체크 (이메일 없음)
        given(userRepository.findByEmail(signUpRequest.getEmail())).willReturn(Optional.empty());

        // save 동작 mock
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            savedUser.setStatus(UserStatus.ACTIVE);
            savedUser.setPasswordChangedAt(LocalDateTime.now());
            UserProfile userProfile = new UserProfile(savedUser, signUpRequest.getName(), signUpRequest.getName(), signUpRequest.getPhone());
            savedUser.setUserProfile(userProfile);
            // 비밀번호가 암호화된 값과 같은지 검증
            assertThat(savedUser.getPassword()).isEqualTo(preEncryptedPassword);
            return savedUser;
        });

        // When
        UserResponse response = userService.signUp(signUpRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(signUpRequest.getEmail());
        assertThat(response.getRole()).isEqualTo(UserRole.USER);
        assertThat(response.getUserId()).isEqualTo(2L);

        // findByEmail만 호출됨
        verify(userRepository, times(1)).findByEmail(signUpRequest.getEmail());
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // 이메일 인증 성공
    @Test
    @DisplayName("이메일 인증 성공")
    void verifyEmail_Success() {
        // Given
        String token = "valid-token";
        User user = new User();
        user.setStatus(UserStatus.PENDING);
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusMinutes(30));
        given(userRepository.findByVerificationToken(token)).willReturn(Optional.of(user));

        // When
        userService.verifyEmail(token);

        // Then
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getVerificationToken()).isNull();
        assertThat(user.getVerificationTokenExpiry()).isNull();
        verify(userRepository, times(1)).save(user);
    }

    // 이메일 인증 실패 - 만료된 토큰
    @Test
    @DisplayName("이메일 인증 실패 - 만료된 토큰")
    void verifyEmail_Fail_ExpiredToken() {
        // Given
        String token = "expired-token";
        User user = new User();
        user.setStatus(UserStatus.PENDING);
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(LocalDateTime.now().minusMinutes(1));
        given(userRepository.findByVerificationToken(token)).willReturn(Optional.of(user));

        // When & Then
        UserException thrown = assertThrows(UserException.class, () ->
                userService.verifyEmail(token)
        );
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.VERIFICATION_TOKEN_EXPIRED);
        verify(userRepository, times(1)).save(user);
    }

    // 이메일 인증 실패 - 잘못된 토큰
    @Test
    @DisplayName("이메일 인증 실패 - 잘못된 토큰")
    void verifyEmail_Fail_InvalidToken() {
        // Given
        String token = "invalid-token";
        given(userRepository.findByVerificationToken(token)).willReturn(Optional.empty());

        // When & Then
        UserException thrown = assertThrows(UserException.class, () ->
                userService.verifyEmail(token)
        );
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.INVALID_VERIFICATION_TOKEN);
    }

    @Test
    @DisplayName("이메일 중복 확인 - 이메일이 이미 존재하는 경우")
    void isEmailDuplicated_True() {
        // Given
        String existingEmail = "duplicate@example.com";
        given(userRepository.existsByEmail(existingEmail)).willReturn(true);

        // When
        boolean isDuplicated = userService.isEmailDuplicated(existingEmail);

        // Then
        assertThat(isDuplicated).isTrue();
        verify(userRepository, times(1)).existsByEmail(existingEmail); // existsByEmail 호출 확인
    }

    @Test
    @DisplayName("이메일 중복 확인 - 이메일이 존재하지 않는 경우")
    void isEmailDuplicated_False() {
        // Given
        String newEmail = "new@example.com";
        given(userRepository.existsByEmail(newEmail)).willReturn(false);

        // When
        boolean isDuplicated = userService.isEmailDuplicated(newEmail);

        // Then
        assertThat(isDuplicated).isFalse();
        verify(userRepository, times(1)).existsByEmail(newEmail); // existsByEmail 호출 확인
    }

    // --- 회원 탈퇴 테스트 ---

    @Test
    @DisplayName("회원 탈퇴 성공 - 사용자 ID로 탈퇴")
    void withdrawUser_Success_ById() {
        // Given
        setupAdminAuthentication();
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setStatus(UserStatus.ACTIVE);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // When
        userService.withdrawUser(userId);

        // Then
        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 사용자 ID로 탈퇴 시 이미 DELETED 상태")
    void withdrawUser_Fail_AlreadyDeleted_ById() {
        // Given
        setupAdminAuthentication();
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setStatus(UserStatus.DELETED);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // When & Then
        UserException thrown = assertThrows(UserException.class, () ->
                userService.withdrawUser(userId)
        );
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_ALREADY_DELETED);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원 탈퇴 성공 - 관리자가 이메일로 탈퇴")
    void adminWithdrawUserByEmail_Success() {
        // Given
        setupAdminAuthentication();
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setStatus(UserStatus.ACTIVE);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // When
        userService.adminWithdrawUserByEmail(email);

        // Then
        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 관리자가 이메일로 탈퇴 시 사용자 없음")
    void adminWithdrawUserByEmail_Fail_UserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin@example.com",
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When & Then
        UserException thrown = assertThrows(UserException.class, () ->
                userService.adminWithdrawUserByEmail(email)
        );
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 관리자가 이메일로 탈퇴 시 이미 DELETED 상태")
    void adminWithdrawUserByEmail_Fail_AlreadyDeleted() {
        // Given
        setupAdminAuthentication();
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setStatus(UserStatus.DELETED);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // When & Then
        UserException thrown = assertThrows(UserException.class, () ->
                userService.adminWithdrawUserByEmail(email)
        );
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_ALREADY_DELETED);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원 탈퇴 성공 - 이메일과 비밀번호로 탈퇴")
    void withdrawUserWithPassword_Success() {
        // Given
        String email = "test@example.com";
        String rawPassword = "password123!";
        String encodedPassword = "encoded_password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setStatus(UserStatus.ACTIVE);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When
        userService.withdrawCurrentUserWithPassword(email, rawPassword);

        // Then
        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 이메일과 비밀번호로 탈퇴 시 사용자 없음")
    void withdrawCurrentUserWithPassword_Fail_UserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        String rawPassword = "password123!";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        String loggedInUserEmail = email;
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loggedInUserEmail, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When & Then
        UserException thrown = assertThrows(UserException.class, () ->
                userService.withdrawCurrentUserWithPassword(email, rawPassword)
        );
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(userRepository, never()).save(any(User.class));

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 이메일과 비밀번호로 탈퇴 시 비밀번호 불일치")
    void withdrawCurrentUserWithPassword_Fail_InvalidPassword() {
        // Given
        String email = "test@example.com";
        String rawPassword = "wrong_password";
        String encodedPassword = "encoded_password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setStatus(UserStatus.ACTIVE);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(false);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When & Then
        UserException thrown = assertThrows(UserException.class, () ->
                userService.withdrawCurrentUserWithPassword(email, rawPassword)
        );
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 이메일과 비밀번호로 탈퇴 시 이미 DELETED 상태")
    void withdrawCurrentUserWithPassword_Fail_AlreadyDeleted() {
        // Given
        String email = "test@example.com";
        String rawPassword = "password123!";
        String encodedPassword = "encoded_password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setStatus(UserStatus.DELETED); // 이미 DELETED 상태

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When & Then
        UserException thrown = assertThrows(UserException.class, () ->
                userService.withdrawCurrentUserWithPassword(email, rawPassword)
        );
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_ALREADY_DELETED);
        verify(userRepository, never()).save(any(User.class));
    }
}