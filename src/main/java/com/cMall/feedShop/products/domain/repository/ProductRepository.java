package com.cMall.feedShop.products.domain.repository;

import com.cMall.feedShop.products.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.productColors pc " +
            "LEFT JOIN FETCH pc.color " +
            "LEFT JOIN FETCH p.productSizes ps " +
            "LEFT JOIN FETCH p.productDiscounts pd " +
            "LEFT JOIN FETCH p.store s")
    List<Product> findAllWithDetails();
}