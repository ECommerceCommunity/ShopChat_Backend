package com.cMall.feedShop.product.domain.repository;

import com.cMall.feedShop.product.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
