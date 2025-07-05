package com.cMall.feedShop.product.domain.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProductImage {
    private Long productImageId;
    private String url;
    private String type;
    private Long productId;
}
