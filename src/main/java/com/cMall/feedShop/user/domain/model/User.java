package com.cMall.feedShop.user.domain.model;

import com.cMall.feedShop.user.domain.enums.UserRole;
import com.cMall.feedShop.user.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

// User.java - 핵심 엔티티부터
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "login_id", unique = true, nullable = false, length = 100)
    private String loginId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED') DEFAULT 'ACTIVE'")
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SELLER') DEFAULT 'ROLE_USER'") // ERD의 role
    private UserRole role;

    @Column(nullable = false, length = 20)
    private String phone;

    //(회원가입 시 사용)
    public User(String loginId, String password, String email, String phone, UserRole role) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = UserStatus.ACTIVE; // 기본 상태 활성화
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.passwordChangedAt = LocalDateTime.now(); // 초기 비밀번호 변경 시간 설정
    }

    // 비즈니스 메서드
    public void changePassword(String newPassword) {
        // 도메인 규칙 검증
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    public boolean canLogin() {
        return status == UserStatus.ACTIVE;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}