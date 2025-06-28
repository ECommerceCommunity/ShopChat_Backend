package com.cMall.feedShop.products.domain;

import com.cMall.feedShop.stores.domain.Store;
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

    private String name;

    private BigDecimal price;

    private String gender;

    @Column(columnDefinition = "TEXT") // 상품 설명은 길이가 길 수 있어 TEXT로 설정
    private String description;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    // 생성자
    public Product(String name, BigDecimal price,
                   String gender, String description,
                   String mainImageUrls, String detailImageUrls) {
        this.name = name;
        this.price = price;
        this.gender = gender;
        this.description = description;
        this.mainImageUrls = mainImageUrls;
        this.detailImageUrls = detailImageUrls;
        this.numbersLikes = 0;
    }

    public void addProductDiscount(ProductDiscount discount) {
        productDiscounts.add(discount);
        discount.setProduct(this); // 양방향 관계 설정
    }
}
