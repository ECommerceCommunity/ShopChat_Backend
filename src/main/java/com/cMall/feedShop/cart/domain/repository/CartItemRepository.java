package com.cMall.feedShop.cart.domain.repository;

import com.cMall.feedShop.cart.domain.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
