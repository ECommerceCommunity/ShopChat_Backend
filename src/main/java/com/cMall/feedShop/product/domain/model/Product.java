package com.cMall.feedShop.product.domain.model;

import com.cMall.feedShop.product.domain.enums.DiscountType;
import com.cMall.feedShop.store.domain.model.Store;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "wish_number")
    private Integer wishNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="discount_type", nullable = false)
    private DiscountType discountType = DiscountType.NONE;

    @Column(name="discount_value")
    private BigDecimal discountValue;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Product(String name, BigDecimal price, Store store, Category category,
                   DiscountType discountType, BigDecimal discountValue, String description) {
        this.name = name;
        this.price = price;
        this.store = store;
        this.category = category;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.description = description;
    }
}
