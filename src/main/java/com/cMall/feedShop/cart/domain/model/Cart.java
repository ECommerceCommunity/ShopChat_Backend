package com.cMall.feedShop.cart.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Cart {
    @Id
    private Long cartId;
    private Long userId;
}
