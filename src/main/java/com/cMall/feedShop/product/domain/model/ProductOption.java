package com.cMall.feedShop.product.domain.model;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class ProductOption {
    private Long optionId;
    private Long productId;
    private String gender;
    private String size;
    private String color;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
