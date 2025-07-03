package com.cMall.feedShop.user.application.dto.request;

import com.cMall.feedShop.annotation.CustomEncryption;
import lombok.Getter;

@Getter
public class UserSignUpRequest {
    private String loginId;
    @CustomEncryption
    private String password;
    private String email;
    private String phone;
}
