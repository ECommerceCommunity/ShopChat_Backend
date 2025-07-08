package com.cMall.feedShop.user.application.service;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock // 목(Mock) 객체로 만들 의존성들
    private UserRepository userRepository;
    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks // 목 객체들을 주입할 대상 (테스트할 서비스)
    private UserService userService;

    private UserSignUpRequest signUpRequest; // 테스트용 회원가입 요청 DTO

    @BeforeEach // 각 테스트 실행 전에 실행되는 초기화 메서드
    void setUp() {
        // Mock UserSignUpRequest 데이터 설정
        signUpRequest = new UserSignUpRequest();
        signUpRequest.setName("테스트유저");
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password123!");
        signUpRequest.setConfirmPassword("password123!");
        signUpRequest.setRole(UserRole.USER);
        signUpRequest.setPhone("01012345678"); // UserProfile에 phone 필드가 있고 NOT NULL이므로 테스트 요청에도 추가
    }

    @Test
    @DisplayName("회원가입 성공 - 일반적인 경우")
    void signUp_Success() {
        // Given (테스트를 위한 사전 조건 설정)
        // 1. 이메일이 존재하지 않음을 가정 (중복 아님)
        given(userRepository.existsByEmail(anyString())).willReturn(false);

        // 2. passwordEncoder.encode() 호출 시 "encoded_password" 반환하도록 Mock 설정
        given(passwordEncoder.encode(anyString())).willReturn("encoded_password");

        // 3. userRepository.save() 호출 시 실제 저장되는 것처럼 동작하도록 Mock 설정
        // 저장될 User 객체를 받아서 ID를 설정하고, UserProfile도 연결하여 반환하도록 함
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User savedUser = invocation.getArgument(0); // save 메서드에 전달된 User 객체를 가져옴
            savedUser.setId(1L); // DB에서 ID가 할당되었다고 가정
            savedUser.setStatus(UserStatus.ACTIVE); // 상태 설정
            savedUser.setPasswordChangedAt(LocalDateTime.now()); // 비밀번호 변경 시간 설정

            // UserProfile 객체 생성 및 User에 연결
            UserProfile userProfile = new UserProfile(savedUser, signUpRequest.getName(), signUpRequest.getName(), signUpRequest.getPhone());
            savedUser.setUserProfile(userProfile); // User 객체에 UserProfile 연결

            return savedUser; // 저장된 User 객체 반환
        });

        // When (테스트 대상 메서드 실행)
        UserResponse response = userService.signUp(signUpRequest);

        // Then (예상 결과 검증)
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(signUpRequest.getEmail());
        assertThat(response.getRole()).isEqualTo(UserRole.USER);
        assertThat(response.getUserId()).isEqualTo(1L);


        // verify (특정 메서드가 호출되었는지, 몇 번 호출되었는지 검증)
        verify(userRepository, times(1)).existsByEmail(signUpRequest.getEmail()); // 이메일 중복 체크 1번 호출
        verify(passwordEncoder, times(1)).encode(signUpRequest.getPassword()); // 비밀번호 인코딩 1번 호출
        verify(userRepository, times(1)).save(any(User.class)); // User 저장 1번 호출
        // UserProfile은 User에 cascade 되어있다면 userProfileRepository.save()가 직접 호출되지 않을 수 있습니다.
        // 그러므로 userProfileRepository에 대한 verify는 생략합니다.
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUp_Fail_EmailDuplication() {
        // Given
        given(userRepository.existsByEmail(anyString())).willReturn(true); // 이메일이 이미 존재함을 가정

        // When & Then (예외 발생 여부 및 메시지 검증)
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                userService.signUp(signUpRequest)
        );

        assertThat(thrown.getMessage()).isEqualTo("이미 사용 중인 이메일입니다.");
        verify(userRepository, times(1)).existsByEmail(signUpRequest.getEmail()); // 이메일 중복 체크만 호출
        verify(passwordEncoder, times(0)).encode(anyString()); // 이메일 중복 시 인코더 호출 안 됨
        verify(userRepository, times(0)).save(any(User.class)); // 이메일 중복 시 저장 안 됨
    }

    @Test
    @DisplayName("회원가입 성공 - 비밀번호가 AOP에 의해 이미 암호화된 경우")
    void signUp_Success_AOPEncryptedPassword() {
        // Given
        String preEncryptedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyza.abcdefghijklmnopqrs."; // AOP가 이미 암호화한 비밀번호
        signUpRequest.setPassword(preEncryptedPassword);
        signUpRequest.setConfirmPassword(preEncryptedPassword); // Confirm password도 동일하게 설정

        // 1. Email not exists
        given(userRepository.existsByEmail(anyString())).willReturn(false);

        // 2. Mock passwordEncoder.encode()
        // AOP에 의해 이미 암호화된 경우 이 메서드는 호출되면 안 됩니다.
        // 따라서 encode()가 호출될 경우 예외를 발생시키거나, 호출되지 않도록 설정합니다.
        // 여기서는 호출되지 않을 것을 예상하므로, given 설정은 필요 없습니다.
        // 만약 호출된다면 Mockito의 verify에서 times(0)으로 확인할 것입니다.

        // 3. Mock userRepository.save
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L); // 다른 ID로 시뮬레이션
            savedUser.setStatus(UserStatus.ACTIVE);
            savedUser.setPasswordChangedAt(LocalDateTime.now());
            savedUser.setCreatedAt(LocalDateTime.now());
            savedUser.setUpdatedAt(LocalDateTime.now());

            // UserProfile 객체 생성 및 User에 연결
            UserProfile userProfile = new UserProfile(savedUser, signUpRequest.getName(), signUpRequest.getName(), signUpRequest.getPhone());
            savedUser.setUserProfile(userProfile);
            return savedUser;
        });

        // When
        UserResponse response = userService.signUp(signUpRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(signUpRequest.getEmail());
        assertThat(response.getRole()).isEqualTo(UserRole.USER);
        assertThat(response.getUserId()).isEqualTo(2L);

        // Verify repository calls
        verify(userRepository, times(1)).existsByEmail(signUpRequest.getEmail());
        // 가장 중요한 검증: passwordEncoder.encode()가 호출되지 않아야 합니다.
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));

        // User 엔티티에 저장된 비밀번호가 요청의 암호화된 비밀번호와 동일한지 확인
        // (이 검증을 위해 save() 시 반환된 User 객체의 비밀번호를 확인할 방법이 필요)
        // 위 given(userRepository.save())의 willAnswer 블록에서 savedUser.getPassword()를 확인하거나,
        // 실제 User 객체의 save 상태를 더 정확히 모의해야 합니다.
        // 여기서는 간략하게 save 호출 검증에 집중합니다.
        // 더 정확한 테스트를 위해선 savedUser.getPassword()가 preEncryptedPassword와 같은지
        // willAnswer 람다 내에서 assert를 추가할 수 있습니다.
        // 예: assertThat(savedUser.getPassword()).isEqualTo(preEncryptedPassword);
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