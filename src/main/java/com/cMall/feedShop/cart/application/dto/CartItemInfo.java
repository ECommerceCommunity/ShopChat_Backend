package com.cMall.feedShop.cart.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartItemInfo {
    private Long cartItemId;
    private Long optionId;
    private Integer quantity;
}
