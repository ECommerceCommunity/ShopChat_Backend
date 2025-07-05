package com.cMall.feedShop.cart.domain.model;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class RecentView {
    private Long viewId;
    private Long userId;
    private Long productId;
    private LocalDateTime viewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
