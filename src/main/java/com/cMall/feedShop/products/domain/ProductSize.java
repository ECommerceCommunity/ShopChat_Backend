package com.cMall.feedShop.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shoes")
@Getter
@NoArgsConstructor
public class ProductSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer size;

    private Integer stock;

    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // 생성자
    public ProductSize(Integer size, Integer stock, Product product, String type) {
        this.size = size;
        this.stock = stock;
        this.product = product;
        this.type = type;
    }
}
