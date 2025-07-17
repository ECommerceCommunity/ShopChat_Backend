package com.cMall.feedShop.review.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SizeFit {
    VERY_SMALL("very_small", "매우 작음"),
    SMALL("small", "작음"),
    PERFECT("perfect", "딱 맞음"),
    BIG("big", "큼"),
    VERY_BIG("very_big", "매우 큼");

    private final String code;
    private final String description;

    public static SizeFit fromCode(String code) {
        for (SizeFit sizeFit : values()) {
            if (sizeFit.getCode().equals(code)) {
                return sizeFit;
            }
        }
        throw new IllegalArgumentException("Unknown SizeFit code: " + code);
    }
}