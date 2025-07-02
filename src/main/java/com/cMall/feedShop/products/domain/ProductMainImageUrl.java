package com.cMall.feedShop.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_main_image_urls")
@Getter
@NoArgsConstructor
public class ProductMainImageUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductMainImageUrl(String imageUrl, Product product) {
        this.imageUrl = imageUrl;
        this.product = product;
    }

    // ✅ 추가: setProduct 메서드
    public void setProduct(Product product) {
        this.product = product;
    }
}
