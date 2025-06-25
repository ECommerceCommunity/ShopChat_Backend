package com.cMall.feedShop.products.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.cMall.feedShop.products.domain.Product;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String mainImageUrls;

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getMainImageUrls()
        );
    }
}
