package com.cMall.feedShop.cart.domain.model;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class CartItem {
    private Long cartItemId;
    private Long cartId;
    private Long optionId;
    private Long imageId;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
