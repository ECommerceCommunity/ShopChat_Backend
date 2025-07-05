package com.cMall.feedShop.product.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class ProductOption {
    @Id
    private Long optionId;
    private Long productId;
    private String gender;
    private String size;
    private String color;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
