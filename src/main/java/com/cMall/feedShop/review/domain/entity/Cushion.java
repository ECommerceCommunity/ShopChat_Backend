package com.cMall.feedShop.review.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Cushion {
    VERY_SOFT("very_soft", "매우 부드러움"),
    SOFT("soft", "부드러움"),
    NORMAL("normal", "보통"),
    FIRM("firm", "단단함"),
    VERY_FIRM("very_firm", "매우 단단함");

    private final String code;
    private final String description;

    public static Cushion fromCode(String code) {
        for (Cushion cushion : values()) {
            if (cushion.getCode().equals(code)) {
                return cushion;
            }
        }
        throw new IllegalArgumentException("Unknown Cushion code: " + code);
    }
}