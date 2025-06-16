package com.cMall.shopChat.user.application.service;

import org.springframework.stereotype.Service;

// UserService.java
@Service
@Transactional
public class UserService {

    public UserResponse signUp(UserSignUpRequest request) {
        // 1. 중복 체크
        // 2. 비밀번호 암호화
        // 3. 사용자 생성
        // 4. 이메일 인증 발송
    }

    public AuthTokenResponse login(UserLoginRequest request) {
        // 1. 사용자 검증
        // 2. 비밀번호 확인
        // 3. JWT 토큰 생성
        // 4. 리프레시 토큰 저장
    }
}
