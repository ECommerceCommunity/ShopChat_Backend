package com.cMall.feedShop.user.presentation;

import com.cMall.feedShop.user.application.dto.request.UserSignUpRequest;
import com.cMall.feedShop.user.application.dto.response.UserProfileResponse; 
import com.cMall.feedShop.user.application.service.UserProfileService;
import com.cMall.feedShop.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserProfileService userProfileService;
    private final UserService userService;


    @GetMapping("/{userId}/profile")
    public UserProfileResponse getUserProfile(@PathVariable Long userId) {
        UserProfileResponse response = userProfileService.getUserProfile(userId);
        return response;
    }

    @PostMapping("/signup")
    public String signUp(@RequestBody UserSignUpRequest request) {
        System.out.println("Received password (after AOP): " + request.getPassword());

        // PasswordEncryptionAspect에 의해 이미 암호화된 DTO를 서비스 계층으로 전달
        userService.signUp(request);

        return "User signed up successfully";
    }

    // JWT 로그인 메서드도 있다면 여기에 추가
    // @PostMapping("/login")
    // public AuthTokenResponse login(@RequestBody UserLoginRequest request) {
    //     return userService.login(request);
    // }
}
