package com.cMall.feedShop.product.application.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ProductResponse {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer wishNumber;
    private String discountType;
    private BigDecimal discountValue;
    private Long storeId;
    private Long categoryId;
    private LocalDateTime createdAt;
}
