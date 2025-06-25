package com.cMall.feedShop.products.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cMall.feedShop.products.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}