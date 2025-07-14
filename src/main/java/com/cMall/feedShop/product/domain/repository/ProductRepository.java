package com.cMall.feedShop.product.domain.repository;

import com.cMall.feedShop.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 상품 목록 조회 (store, productImages 포함)
    @EntityGraph(attributePaths = {"store", "category", "productImages"})
    Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 상품 상세 조회 (모든 연관 포함)
    @EntityGraph(attributePaths = {"store", "category", "productImages"})
    Optional<Product> findByProductId(Long productId);
}
