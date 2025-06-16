package com.cMall.shopChat.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

// User.java - 핵심 엔티티부터
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_USER;

    // 비즈니스 메서드
    public void changePassword(String newPassword) {
        // 도메인 규칙 검증
    }

    public boolean canLogin() {
        return status == UserStatus.ACTIVE;
    }
}