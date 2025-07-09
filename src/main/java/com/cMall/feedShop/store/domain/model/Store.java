package com.cMall.feedShop.store.domain.model;

import com.cMall.feedShop.product.domain.model.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "stores")
@Getter
@NoArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo")
    private String logo;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Store(String storeName, Long sellerId, String description, String logo) {
        this.storeName = storeName;
        this.sellerId = sellerId;
        this.description = description;
        this.logo = logo;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isManagedBy(Long userId) {
        return Objects.equals(this.sellerId, userId);
    }
}
