package com.cMall.feedShop.products.domain.repository;

import com.cMall.feedShop.products.domain.DiscountPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<DiscountPrice, Long> {
}
