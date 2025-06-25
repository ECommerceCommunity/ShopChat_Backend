package com.cMall.feedShop.products.application.dto.request;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class ProductCreateRequest {
    private Long storeId;
    private Long userId;
    private String name;
    private String shoesType;
    private BigDecimal price;
    private String sizeStockList;
    private String gender;
    private String mainImageUrls;
    private String detailImageUrls;
    private String colorInfo;
}
