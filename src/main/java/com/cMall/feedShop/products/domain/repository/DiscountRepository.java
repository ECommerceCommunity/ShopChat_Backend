package com.cMall.feedShop.products.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cMall.feedShop.products.domain.ProductDiscount;

public interface DiscountRepository extends JpaRepository<ProductDiscount, Long> {
}
