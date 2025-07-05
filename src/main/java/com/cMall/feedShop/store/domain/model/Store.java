package com.cMall.feedShop.store.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity()
@NoArgsConstructor
public class Store {
    @Id
    private Long storeId;
    private String storeName;
    private String description;
    private String logo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long managerId;
}
