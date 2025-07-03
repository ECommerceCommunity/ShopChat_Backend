package com.cMall.feedShop.user.domain.service;

public interface PasswordEncryptionService {
    String encrypt(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}