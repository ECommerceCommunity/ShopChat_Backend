package com.cMall.feedShop.product.domain.model;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
public class Product {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer wishNumber;
    private String discountType;
    private BigDecimal discountValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long storeId;
    private Long categoryId;
}
