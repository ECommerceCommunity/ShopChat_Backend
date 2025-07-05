package com.cMall.feedShop.cart.domain.model;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class Wishlist {
    private Long wishlistId;
    private Long userId;
    private Long productId;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
