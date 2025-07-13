package com.cMall.feedShop.product.domain.repository;

import com.cMall.feedShop.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 삭제되지 않은 상품들을 페이징으로 조회
     * @EntityGraph로 Store를 함께 fetch
     */
    @EntityGraph(attributePaths = {"store"})
    Page<Product> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);
}
