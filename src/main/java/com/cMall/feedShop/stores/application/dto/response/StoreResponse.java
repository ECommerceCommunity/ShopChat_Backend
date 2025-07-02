package com.cMall.feedShop.stores.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreResponse {
    private Long id;
    private String name;
    private String description;
    private String logo;
    private int numbersLikes;
}
