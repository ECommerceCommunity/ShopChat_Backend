package com.cMall.feedShop.products.domain;

import com.cMall.feedShop.stores.domain.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

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

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "numbers_likes")
    private Integer numbersLikes;

    @Column(name = "model_code")
    private String modelCode;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductColor> productColors = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private List<ProductSize> productSizes;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDiscount> productDiscounts = new ArrayList<>();

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<ProductMainImageUrl> mainImageUrls;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<ProductDetailImageUrl> detailImageUrls;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    // 생성자
    public Product(String name, BigDecimal price,
                   String gender, String description,
                   String modelCode) {
        this.name = name;
        this.price = price;
        this.gender = gender;
        this.description = description;
        this.numbersLikes = 0;
        this.modelCode = modelCode;
    }

    // 양방향 관계 설정 메서드
    public void addProductDiscount(ProductDiscount discount) {
        productDiscounts.add(discount);
        discount.setProduct(this);
    }

    public void addProductColor(ProductColor color) {
        productColors.add(color);
        color.setProduct(this);
    }

    public void addProductSize(ProductSize size) {
        productSizes.add(size);
        size.setProduct(this);
    }

    public void addMainImageUrl(ProductMainImageUrl imageUrl) {
        mainImageUrls.add(imageUrl);
        imageUrl.setProduct(this);
    }

    public void addDetailImageUrl(ProductDetailImageUrl imageUrl) {
        detailImageUrls.add(imageUrl);
        imageUrl.setProduct(this);
    }
}
