package com.cMall.feedShop.product.domain.repository;

import com.cMall.feedShop.product.domain.model.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
}
