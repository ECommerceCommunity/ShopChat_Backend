package com.cMall.feedShop.product.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Category {
    @Id
    private Long categoryId;
    private String type;
}
