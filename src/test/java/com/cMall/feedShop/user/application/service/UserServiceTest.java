
package com.cMall.feedShop.user.application.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

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
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                userService.signUp(signUpRequest)
        );
        assertThat(thrown.getMessage()).isEqualTo("이미 사용 중인 이메일입니다.");
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

        // passwordEncoder.encode()는 호출되지 않아야 함
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
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                userService.verifyEmail(token)
        );
        assertThat(thrown.getMessage()).contains("인증 토큰이 만료되었습니다");
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
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                userService.verifyEmail(token)
        );
        assertThat(thrown.getMessage()).contains("유효하지 않거나 찾을 수 없는 인증 토큰");
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
}
