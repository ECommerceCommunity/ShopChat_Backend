package com.cMall.feedShop.user.application.service;

import com.cMall.feedShop.user.domain.exception.UserException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.cMall.feedShop.common.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.verification-url}")
    private String verificationUrl;


    public UserResponse signUp(UserSignUpRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            if (user.getStatus() == UserStatus.ACTIVE) {
                throw new UserException(DUPLICATE_EMAIL);
            }
            if (user.getStatus() == UserStatus.PENDING) {
                updateVerificationToken(user);
                sendVerificationEmail(user, "회원가입 재인증을 완료해주세요.", "회원가입 재인증을 요청하셨습니다. 아래 링크를 클릭하여 이메일 인증을 완료해주세요:");
                throw new UserException(DUPLICATE_EMAIL, "재인증 메일이 발송되었습니다. 메일을 확인하여 인증을 완료해주세요.");
            }
        });

        String finalPasswordToSave;
        if (request.getPassword().startsWith("$2a$") || request.getPassword().startsWith("$2b$") || request.getPassword().startsWith("$2y$")) {
            finalPasswordToSave = request.getPassword();
        } else {
            finalPasswordToSave = passwordEncoder.encode(request.getPassword());
        }

        User user = createUser(request, finalPasswordToSave);
        sendVerificationEmail(user, "회원가입을 완료해주세요.", "cMall 회원가입을 환영합니다. 아래 링크를 클릭하여 이메일 인증을 완료해주세요:");

        return UserResponse.from(user);
    }

    private User createUser(UserSignUpRequest request, String encodedPassword) {
        String generatedLoginId = UUID.randomUUID().toString();

        User user = new User(
                generatedLoginId,
                encodedPassword,
                request.getEmail(),
                UserRole.USER
        );
        user.setStatus(UserStatus.PENDING);
        user.setPasswordChangedAt(LocalDateTime.now());
        updateVerificationToken(user);

        UserProfile userProfile = new UserProfile(
                user,
                request.getName(),
                request.getName(),
                request.getPhone()
        );
        user.setUserProfile(userProfile);

        return userRepository.save(user);
    }

    private void updateVerificationToken(User user) {
        String newVerificationToken = UUID.randomUUID().toString();
        LocalDateTime newExpiryTime = LocalDateTime.now().plusHours(1);
        user.setVerificationToken(newVerificationToken);
        user.setVerificationTokenExpiry(newExpiryTime);
    }

    private void sendVerificationEmail(User user, String subject, String contentBody) {
        String verificationLink = verificationUrl + user.getVerificationToken();
        String emailSubject = "[cMall] " + subject;
        String emailContent = "안녕하세요, " + user.getUserProfile().getName() + "!\n\n" +
                contentBody + "\n\n" +
                verificationLink + "\n\n" +
                "본 링크는 1시간 후 만료됩니다.\n" +
                "감사합니다.\ncMall 팀 드림";

        emailService.sendSimpleEmail(user.getEmail(), emailSubject, emailContent);
    }


    @Transactional(readOnly = true)
    public boolean isLoginIdDuplicated(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }


    @Transactional(readOnly = true)
    public boolean isEmailDuplicated(String email) {
        return userRepository.existsByEmail(email);
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


    @Transactional
    public void withdrawUser(Long userId) {
        checkAdminAuthority("withdrawUser");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND, "사용자를 찾을 수 없습니다. ID: " + userId));
        deleteUser(user);
    }


    @Transactional
    public void adminWithdrawUserByEmail(String email) {
        checkAdminAuthority("adminWithdrawUserByEmail");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND, "사용자를 찾을 수 없습니다. 이메일: " + email));
        deleteUser(user);
    }


    @Transactional
    public void withdrawCurrentUserWithPassword(String email, String rawPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserException(UNAUTHORIZED, "로그인된 사용자만 탈퇴할 수 있습니다.");
        }
        String currentLoggedInUserEmail = authentication.getName();
        if (!currentLoggedInUserEmail.equals(email)) {
            log.warn("Forbidden withdrawal attempt: User '{}' tried to delete account of '{}'.", currentLoggedInUserEmail, email);
            throw new UserException(FORBIDDEN, "다른 사용자의 계정을 탈퇴할 수 없습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND, "사용자를 찾을 수 없습니다. 이메일: " + email));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UserException(INVALID_PASSWORD);
        }

        deleteUser(user);
    }

    private void deleteUser(User user) {
        if (user.getStatus() == UserStatus.DELETED) {
            throw new UserException(USER_ALREADY_DELETED);
        }
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
        // TODO: 사용자와 관련된 다른 데이터 (주문, 게시글, 댓글 등) 처리 로직 추가
    }

    private void checkAdminAuthority(String methodName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .noneMatch(role -> role.equals("ROLE_ADMIN"))) {
            String requester = (authentication != null) ? authentication.getName() : "anonymous";
            log.warn("Unauthorized access attempt to '{}' by user '{}'", methodName, requester);
            throw new UserException(FORBIDDEN, "관리자 권한이 필요합니다.");
        }
    }
}