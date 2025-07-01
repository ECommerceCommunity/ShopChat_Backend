package com.cMall.feedShop.products.domain.repository;

import com.cMall.feedShop.products.domain.Product;
import com.cMall.feedShop.products.application.dto.response.OtherColorProductDto;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN FETCH p.store " +
            "ORDER BY p.id DESC")
    List<Product> findAllWithDetails();

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.store " +
            "WHERE p.id = :id")
    Product findByIdWithDetails(Long id);

    @EntityGraph(attributePaths = {"store"})
    Page<Product> findAll(Pageable pageable);

    @Query("SELECT new com.cMall.feedShop.products.application.dto.response.OtherColorProductDto(p.id, p.thumbnailUrl) " +
            "FROM Product p WHERE p.modelCode = :modelCode AND p.id <> :currentProductId")
    List<OtherColorProductDto> findOtherColorProductsByModelCode(String modelCode, Long currentProductId);
}
