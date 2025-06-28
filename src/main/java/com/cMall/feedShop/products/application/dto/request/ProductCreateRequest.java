package com.cMall.feedShop.products.application.dto.request;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class ProductCreateRequest {
    private String name;
    private BigDecimal price;
    private String gender;
    private String description;
    private String mainImageUrls;
    private String detailImageUrls;
    private List<Long> colorIds;
    private List<OptionDto> options;

    @Getter
    public static class OptionDto {
        private Integer size;
        private Integer stock;
        private String type;
    }
}
