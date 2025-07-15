package com.cMall.feedShop.user.application.service;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;
import com.cMall.feedShop.common.service.EmailService;
import com.cMall.feedShop.user.application.dto.request.UserSignUpRequest;
import com.cMall.feedShop.user.application.dto.response.UserResponse;
import com.cMall.feedShop.user.domain.exception.UserException;
import com.cMall.feedShop.user.domain.model.User;
import com.cMall.feedShop.user.domain.enums.UserRole;
import com.cMall.feedShop.user.domain.enums.UserStatus;
import com.cMall.feedShop.user.domain.model.UserProfile;
import com.cMall.feedShop.user.domain.repository.UserProfileRepository;
import com.cMall.feedShop.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.cMall.feedShop.common.exception.ErrorCode.*;

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
        user.setPasswordChangedAt(LocalDateTime.now());

        String verificationToken = UUID.randomUUID().toString();

        LocalDateTime tokenExpiryTime = LocalDateTime.now().plusHours(1); // 예: 1시간 후 만료

        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(tokenExpiryTime);

        UserProfile userProfile = new UserProfile(
                user,
                request.getName(),
                request.getName(),
                request.getPhone()
        );

        user.setUserProfile(userProfile);

        userRepository.save(user);


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

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new UserException(INVALID_VERIFICATION_TOKEN));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new UserException(ACCOUNT_ALREADY_VERIFIED);
        }

        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            user.setVerificationToken(null);
            user.setVerificationTokenExpiry(null);
            userRepository.save(user);
            throw new UserException(VERIFICATION_TOKEN_EXPIRED);
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
    }

    // 1. 사용자 ID로 회원 탈퇴 (관리자용 또는 내부 로직) - 기존 코드 유지
    @Transactional
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(INVALID_VERIFICATION_TOKEN));
        if (user.getStatus() == UserStatus.DELETED) {
            return;
        }

        // 옵션 A: 사용자 상태를 DELETED로 변경 (소프트 삭제) - 일반적으로 선호
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);

        // TODO: 사용자와 관련된 다른 데이터 (주문, 게시글, 댓글 등) 처리 로직 추가
        // - 해당 사용자의 모든 게시글/댓글을 삭제 (Hard Delete) 또는 작성자를 '탈퇴한 사용자' 등으로 변경 (Soft Delete)
        // - 해당 사용자의 주문 내역은 유지하되, 사용자 정보는 비식별화 (개인정보보호)
        // - 예시: orderService.anonymizeUserOrders(userId);
        // - 예시: boardService.updateAuthorToWithdrawn(userId);
    }

    // 2. 관리자용: 이메일로 사용자 탈퇴 (비밀번호 확인 불필요)
    @Transactional
    public void adminWithdrawUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,"사용자를 찾을 수 없습니다. 이메일: " + email));
        if (user.getStatus() == UserStatus.DELETED) {
            return;
        }
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);

        // TODO: 위 withdrawUser 메서드와 동일하게 관련 데이터 처리 로직 추가
    }

    // 3. 사용자용: 이메일과 비밀번호 확인으로 회원 탈퇴 (보안 강화) - 기존 코드 유지
    @Transactional
    public void withdrawCurrentUserWithPassword(String email, String rawPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "로그인된 사용자만 탈퇴할 수 있습니다.");
        }
        String currentLoggedInUserEmail = authentication.getName();
        if (!currentLoggedInUserEmail.equals(email)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "다른 사용자의 계정을 탈퇴할 수 없습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,"사용자를 찾을 수 없습니다. 이메일: " + email));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD,"비밀번호가 일치하지 않습니다.");
        }

        if (user.getStatus() == UserStatus.DELETED) {
            throw new BusinessException(ErrorCode.USER_ALREADY_DELETED,"이미 탈퇴 처리된 계정입니다.");
        }

        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);

        // TODO: 위 withdrawUser 메서드와 동일하게 관련 데이터 처리 로직 추가
    }
}