package com.cMall.feedShop.cart.domain.repository;

import com.cMall.feedShop.cart.domain.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
}
