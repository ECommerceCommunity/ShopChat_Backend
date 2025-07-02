package com.cMall.feedShop.products.domain.repository;

import com.cMall.feedShop.products.domain.ProductOtherColor;
import com.cMall.feedShop.products.application.dto.response.OtherColorProductDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductOtherColorRepository extends JpaRepository<ProductOtherColor, Long> {

    @Query("SELECT new com.cMall.feedShop.products.application.dto.response.OtherColorProductDto(poc.otherProduct.id, poc.otherProduct.name, poc.thumbnailUrl) " +
            "FROM ProductOtherColor poc WHERE poc.product.id = :productId")
    List<OtherColorProductDto> findOtherColorsByProductId(Long productId);
}