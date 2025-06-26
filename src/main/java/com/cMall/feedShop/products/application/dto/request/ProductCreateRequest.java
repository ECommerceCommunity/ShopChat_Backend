package com.cMall.feedShop.products.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    private String name;
    private String shoesType;
    private BigDecimal price;
    private String gender;
    private String mainImageUrls;
    private String detailImageUrls;
    private List<Long> colorIds;

    private List<OptionDto> options;

    @Getter
    @NoArgsConstructor
    public static class OptionDto {
        private String size;
        private Integer stock;
    }
}
