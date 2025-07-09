package com.cMall.feedShop.product.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class ProductImage {
    @Id
    private Long productImageId;
    private String url;
    private String type;
    private Long productId;
}
