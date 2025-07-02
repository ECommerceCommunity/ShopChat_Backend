package com.cMall.feedShop.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_detail_image_urls")
@Getter
@NoArgsConstructor
public class ProductDetailImageUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductDetailImageUrl(String imageUrl, Product product) {
        this.imageUrl = imageUrl;
        this.product = product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}