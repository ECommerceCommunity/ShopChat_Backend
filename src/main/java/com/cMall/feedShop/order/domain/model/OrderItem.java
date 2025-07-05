package com.cMall.feedShop.order.domain.model;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
public class OrderItem {
    private Long orderItemId;
    private Long orderId;
    private Long optionId;
    private Long imageId;
    private Integer quantity;
    private BigDecimal discountPrice;
    private BigDecimal price;
    private LocalDateTime orderedAt;
}
