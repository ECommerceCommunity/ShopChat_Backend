package com.cMall.feedShop.user.application.dto.response;

import com.cMall.feedShop.user.domain.model.User;
import com.cMall.feedShop.user.domain.model.UserProfile;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserProfileResponse {
    private Long userId;
    private String username;
    private String email;
    private String nickname;
//    private String profileImageUrl; // 프로필 이미지 URL
//    private String bio; // 자기소개

    // 엔티티를 DTO로 변환하는 정적 팩토리 메서드 (권장되는 패턴)
    public static UserProfileResponse from(User user, UserProfile userProfile) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(userProfile != null ? userProfile.getNickname() : null)
//                .profileImageUrl(userProfile != null ? userProfile.getProfileImageUrl() : null)
//                .bio(userProfile != null ? userProfile.getBio() : null)
                .build();
    }

    // 또는 User 엔티티만으로도 만들 수 있도록 오버로드
    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}