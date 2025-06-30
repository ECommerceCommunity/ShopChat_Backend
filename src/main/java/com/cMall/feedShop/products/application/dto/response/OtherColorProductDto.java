package com.cMall.feedShop.products.application.dto.response;

import lombok.Getter;

@Getter
public class OtherColorProductDto {
    private Long productId;
    private String thumbnailUrl;

    public OtherColorProductDto(Long productId, String thumbnailUrl) {
        this.productId = productId;
        this.thumbnailUrl = thumbnailUrl;
    }
}
