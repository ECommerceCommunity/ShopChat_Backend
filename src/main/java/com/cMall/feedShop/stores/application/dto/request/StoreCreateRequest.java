package com.cMall.feedShop.stores.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreCreateRequest {

    private Long userId;

    private String name;
    private String description;
    private String logo;
}
