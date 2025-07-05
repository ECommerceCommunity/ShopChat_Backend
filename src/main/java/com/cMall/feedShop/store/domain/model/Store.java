package com.cMall.feedShop.store.domain.model;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class Store {
    private Long storeId;
    private String storeName;
    private String description;
    private String logo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long managerId;
}
