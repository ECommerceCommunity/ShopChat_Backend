package com.cMall.feedShop.user.application.service;

import com.cMall.feedShop.common.service.EmailService;
import com.cMall.feedShop.user.application.dto.request.UserSignUpRequest;
import com.cMall.feedShop.user.application.dto.response.UserResponse;
import com.cMall.feedShop.user.domain.model.User;
import com.cMall.feedShop.user.domain.enums.UserRole;
import com.cMall.feedShop.user.domain.enums.UserStatus;
import com.cMall.feedShop.user.domain.model.UserProfile;
import com.cMall.feedShop.user.domain.repository.UserProfileRepository;
import com.cMall.feedShop.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserResponse signUp(UserSignUpRequest request) {

        // 이메일 중복 확인 및 상태에 따른 처리
        Optional<User> existingUserOptional = userRepository.findByEmail(request.getEmail());

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            if (existingUser.getStatus() == UserStatus.ACTIVE) {
                // 이미 활성(ACTIVE) 상태의 사용자가 해당 이메일로 가입되어 있다면
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            } else if (existingUser.getStatus() == UserStatus.PENDING) {
                // PENDING 상태의 사용자가 존재한다면 (이메일 인증 미완료)
                // 기존 PENDING 계정의 인증 토큰 및 만료 시간 업데이트
                String newVerificationToken = UUID.randomUUID().toString();
                LocalDateTime newExpiryTime = LocalDateTime.now().plusHours(1);

                existingUser.setVerificationToken(newVerificationToken);
                existingUser.setVerificationTokenExpiry(newExpiryTime);
                userRepository.save(existingUser); // 업데이트된 사용자 정보 저장

                // 사용자에게 재인증 메일 전송
                String verificationLink = "https://localhost:8443/api/auth/verify-email?token=" + newVerificationToken;
                String emailSubject = "[cMall] 회원가입 재인증을 완료해주세요.";
                String emailContent = "안녕하세요, " + existingUser.getUserProfile().getName() + "님!\n\n" +
                        "회원가입 재인증을 요청하셨습니다. 아래 링크를 클릭하여 이메일 인증을 완료해주세요:\n\n" +
                        verificationLink + "\n\n" +
                        "본 링크는 1시간 후 만료됩니다.\n" +
                        "감사합니다.\ncMall 팀 드림";

                emailService.sendSimpleEmail(existingUser.getEmail(), emailSubject, emailContent);

                return UserResponse.from(
                        existingUser,
                        "재인증 메일이 발송되었습니다. 메일을 확인하여 인증을 완료해주세요."
                );
            }
            // 기타 다른 상태 (DELETED 등)에 대한 처리도 추가할 수 있습니다.
        }

        // loginId 자동 생성 및 중복 확인
        // UI에서 loginId를 받지 않으므로 UUID로 자동 생성
        String generatedLoginId = UUID.randomUUID().toString();

        // 4. 비밀번호 암호화
        String finalPasswordToSave;
        if (request.getPassword().startsWith("$2a$") || request.getPassword().startsWith("$2b$") || request.getPassword().startsWith("$2y$")) {
            finalPasswordToSave = request.getPassword();
        } else {
            finalPasswordToSave = passwordEncoder.encode(request.getPassword());
        }

        // 5. User 엔티티 생성 및 초기화
        User user = new User(
                generatedLoginId,
                finalPasswordToSave,
                request.getEmail(),
                UserRole.USER
        );
        user.setStatus(UserStatus.PENDING);
        user.setPasswordChangedAt(LocalDateTime.now()); // 초기 비밀번호 변경 시간 설정

        String verificationToken = UUID.randomUUID().toString();

        LocalDateTime tokenExpiryTime = LocalDateTime.now().plusHours(1); // 예: 1시간 후 만료

        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(tokenExpiryTime);

        // 6. UserProfile 엔티티 생성
        UserProfile userProfile = new UserProfile(
                user,
                request.getName(),
                request.getName(),
                request.getPhone()
        );

        user.setUserProfile(userProfile);

        userRepository.save(user); // User에 cascade = CascadeType.ALL 설정 시 UserProfile도 함께 저장됨


        // 이메일 전송
        String verificationLink = "https://localhost:8443/api/auth/verify-email?token=" + verificationToken;
        String emailSubject = "[cMall] 회원가입을 완료해주세요.";
        String emailContent = "안녕하세요, " + request.getName() + "님!\n\n" +
                "cMall 회원가입을 환영합니다. 아래 링크를 클릭하여 이메일 인증을 완료해주세요:\n\n" +
                verificationLink + "\n\n" +
                "본 링크는 1시간 후 만료됩니다.\n" +
                "감사합니다.\ncMall 팀 드림";

        emailService.sendSimpleEmail(request.getEmail(), emailSubject, emailContent);

        // 9. 응답 DTO 반환
        return UserResponse.from(user);
    }

    // 아이디 중복 확인 메서드 (API 제공 시 활용)
    @Transactional(readOnly = true)
    public boolean isLoginIdDuplicated(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    // 이메일 중복 확인 메서드 (API 제공 시 활용)
    @Transactional(readOnly = true)
    public boolean isEmailDuplicated(String email) {
        return userRepository.existsByEmail(email); // User Repository 사용
    }

    /**
     * 이메일 인증 토큰을 검증하고 사용자 계정을 활성화합니다.
     * @param token 사용자가 이메일 링크를 통해 전달한 인증 토큰
     * @throws RuntimeException 토큰이 유효하지 않거나 만료되었을 경우 등
     */
    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("유효하지 않거나 찾을 수 없는 인증 토큰입니다."));


        // 토큰이 유효한지 (만료되지 않았는지, 이미 사용되었는지) 확인합니다.

        if (user.getStatus() == UserStatus.ACTIVE) {
            user.setVerificationToken(null);
            user.setVerificationTokenExpiry(null);
            userRepository.save(user);
            throw new RuntimeException("이미 인증이 완료된 계정입니다.");
        }

        if (user.getVerificationToken() == null || !user.getVerificationToken().equals(token)) {
            throw new RuntimeException("인증 토큰이 유효하지 않습니다.");
        }

        // 토큰이 만료되었는지 확인
        // verificationTokenExpiry가 null이거나 현재 시간보다 이전이면 만료된 것으로 간주
        if (user.getVerificationTokenExpiry() == null || user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {

            user.setVerificationToken(null);
            user.setVerificationTokenExpiry(null);
            userRepository.save(user); // 변경사항 저장
            throw new RuntimeException("인증 토큰이 만료되었습니다. 다시 회원가입을 시도하거나 인증 메일을 재발송해주세요.");
        }

        user.setStatus(UserStatus.ACTIVE);

        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);

        userRepository.save(user);
    }
}