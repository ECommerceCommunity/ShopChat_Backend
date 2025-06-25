package com.cMall.feedShop.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "user_id")
    private Long userId;

    private String name;

    @Column(name = "shoes_type")
    private String shoesType;

    private BigDecimal price;

    @Column(name = "size_stock_list", columnDefinition = "TEXT")
    private String sizeStockList;

    private String gender;

    @Column(name = "numbers_likes")
    private Integer numbersLikes;

    @Column(name = "main_image_urls")
    private String mainImageUrls;

    @Column(name = "detail_image_urls", columnDefinition = "TEXT")
    private String detailImageUrls;

    @Column(name = "color_info", columnDefinition = "TEXT")
    private String colorInfo;

    public Product(Long storeId, Long userId, String name, String shoesType, BigDecimal price, String sizeStockList,
                   String gender, String mainImageUrls, String detailImageUrls, String colorInfo) {
        this.storeId = storeId;
        this.userId = userId;
        this.name = name;
        this.shoesType = shoesType;
        this.price = price;
        this.sizeStockList = sizeStockList;
        this.gender = gender;
        this.mainImageUrls = mainImageUrls;
        this.detailImageUrls = detailImageUrls;
        this.colorInfo = colorInfo;
        this.numbersLikes = 0;
    }
}