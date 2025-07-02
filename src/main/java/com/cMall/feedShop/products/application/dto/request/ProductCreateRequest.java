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
    private String modelCode;
    private List<String> mainImageUrls;
    private List<String> detailImageUrls;
    private List<Long> colorIds;
    private List<OptionDto> options;

    @Getter
    public static class OptionDto {
        private String size;
        private Integer stock;
        private String type;
    }
}
