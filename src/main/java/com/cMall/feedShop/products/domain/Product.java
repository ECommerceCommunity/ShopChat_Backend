package com.cMall.feedShop.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "store_id")
    private Long storeId;

    private String name;

    @Column(name = "shoes_type")
    private String shoesType;

    private BigDecimal price;

    private String gender;

    @Column(name = "numbers_likes")
    private Integer numbersLikes;

    @Column(name = "main_image_urls")
    private String mainImageUrls;

    @Column(name = "detail_image_urls", columnDefinition = "TEXT")
    private String detailImageUrls;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductColor> productColors = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSize> productSizes = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDiscount> productDiscounts = new ArrayList<>();

    // 생성자
    public Product(Long storeId, String name, String shoesType, BigDecimal price,
                   String gender, String mainImageUrls, String detailImageUrls) {
        this.storeId = storeId;
        this.name = name;
        this.shoesType = shoesType;
        this.price = price;
        this.gender = gender;
        this.mainImageUrls = mainImageUrls;
        this.detailImageUrls = detailImageUrls;
        this.numbersLikes = 0;
    }

    public void addProductDiscount(ProductDiscount discount) {
        productDiscounts.add(discount);
        discount.setProduct(this); // 양방향 관계 설정
    }
}