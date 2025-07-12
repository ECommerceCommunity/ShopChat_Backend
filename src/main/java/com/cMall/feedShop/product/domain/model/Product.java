package com.cMall.feedShop.product.domain.model;

import com.cMall.feedShop.common.BaseTimeEntity;
import com.cMall.feedShop.product.application.util.DiscountCalculator;
import com.cMall.feedShop.product.domain.enums.DiscountType;
import com.cMall.feedShop.product.domain.enums.ImageType;
import com.cMall.feedShop.store.domain.model.Store;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "description")
    private String description;

    @Column(name = "wish_number", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer wishNumber = 0;

    @Enumerated(EnumType.STRING)
    @Column(name="discount_type", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'NONE'")
    private DiscountType discountType = DiscountType.NONE;

    @Column(name="discount_value")
    private BigDecimal discountValue;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> productImages = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductOption> productOptions = new ArrayList<>();

    @Builder
    public Product(String name, BigDecimal price, Store store, Category category,
                   DiscountType discountType, BigDecimal discountValue, String description) {
        this.name = name;
        this.price = price;
        this.store = store;
        this.category = category;
        this.discountType = discountType != null ? discountType : DiscountType.NONE;
        this.discountValue = discountValue;
        this.description = description;
        this.wishNumber = 0;
    }

    // 대표 이미지 조회 (목록에서 필요)
    public String getMainImageUrl() {
        return productImages.stream()
                .filter(image -> ImageType.MAIN.equals(image.getType()))
                .findFirst()
                .map(ProductImage::getUrl)
                .orElse(null);
    }

    // 할인가 계산
    public BigDecimal getDiscountPrice(DiscountCalculator discountCalculator) {
        return discountCalculator.calculateDiscountPrice(
                this.price,
                this.discountType,
                this.discountValue
        );
    }
}
