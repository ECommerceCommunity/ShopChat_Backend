package com.cMall.feedShop.cart.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class CartItem {
    @Id
    private Long cartItemId;
    private Long cartId;
    private Long optionId;
    private Long imageId;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
