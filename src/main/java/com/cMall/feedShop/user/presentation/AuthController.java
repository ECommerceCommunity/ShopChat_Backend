//package com.cMall.feedShop.user.presentation;
//
//import com.cMall.shopChat.user.application.dto.request.UserLoginRequest;
//import com.cMall.shopChat.user.application.dto.request.UserSignUpRequest;
//import com.cMall.shopChat.user.application.dto.response.AuthTokenResponse;
//import com.cMall.shopChat.user.application.dto.response.UserResponse;
//import com.cMall.feedShop.user.application.service.UserService;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    private final UserService userService;
//
//    public AuthController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @PostMapping("/signup")
//    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody UserSignUpRequest request) {
//        return ResponseEntity.ok(userService.signUp(request));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody UserLoginRequest request) {
//        return ResponseEntity.ok(userService.login(request));
//    }
//}