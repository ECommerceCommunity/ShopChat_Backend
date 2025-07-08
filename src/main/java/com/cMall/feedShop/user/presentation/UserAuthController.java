package com.cMall.feedShop.user.presentation; // 현재 패키지 유지, 필요시 com.cMall.feedShop.auth.presentation으로 변경 권장

import com.cMall.feedShop.user.application.dto.request.UserLoginRequest;

import com.cMall.feedShop.user.application.dto.request.UserSignUpRequest;
import com.cMall.feedShop.user.application.dto.response.UserLoginResponse;
import com.cMall.feedShop.user.application.dto.response.UserResponse;
import com.cMall.feedShop.user.application.service.UserAuthService;
import com.cMall.feedShop.user.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor; // Lombok 임포트
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserService userService;
    private final UserAuthService userAuthService;


    @PostMapping("/signup") // POST /api/auth/signup 요청 처리
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody UserSignUpRequest request) {
        return ResponseEntity.ok(userService.signUp(request));
    }

    @PostMapping("/login") // POST /api/auth/login 요청 처리 (React 코드와 일치)
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userAuthService.login(request));
    }

    @GetMapping("/verify-email") // <-- 이 부분이 URL의 /verify-email 경로와 GET 요청을 담당합니다.
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        // 실제 이메일 인증 로직은 UserService가 수행합니다.
        userService.verifyEmail(token);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다. 이제 로그인할 수 있습니다.");
    }
}
