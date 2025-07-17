package com.cMall.feedShop.review.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Stability {
    VERY_UNSTABLE("very_unstable", "매우 불안정"),
    UNSTABLE("unstable", "불안정"),
    NORMAL("normal", "보통"),
    STABLE("stable", "안정적"),
    VERY_STABLE("very_stable", "매우 안정적");

    private final String code;
    private final String description;

    public static Stability fromCode(String code) {
        for (Stability stability : values()) {
            if (stability.getCode().equals(code)) {
                return stability;
            }
        }
        throw new IllegalArgumentException("Unknown Stability code: " + code);
    }
}