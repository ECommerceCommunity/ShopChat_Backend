package com.cMall.feedShop.stores.application.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreCreateRequest {
    private String name;
    private String description;
    private String logo;
}
