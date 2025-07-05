package com.cMall.feedShop.product.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class Product {
    @Id
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
