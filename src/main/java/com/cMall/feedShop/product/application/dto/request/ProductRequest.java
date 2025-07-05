package com.cMall.feedShop.product.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ProductRequest {
    private String name;
    private BigDecimal price;
    private Long storeId;
    private Long categoryId;
}
