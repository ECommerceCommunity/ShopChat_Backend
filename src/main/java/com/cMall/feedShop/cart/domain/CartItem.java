package com.cMall.feedShop.cart.domain;

import com.cMall.feedShop.products.domain.Product;
import com.cMall.feedShop.products.domain.ProductSize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items",
        uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_cart_product_size",
                columnNames = {"cart_id", "product_id", "product_size_id"}
        )
    })
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_size_id", nullable = false)
    private ProductSize productSize;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(nullable = false)
    private Boolean selected = true;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "original_price", nullable = false)
    private BigDecimal originalPrice;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;

    @Column(name = "thumbnail_image_url")
    private String thumbnailImageUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 생성자 및 비즈니스 메서드
    // (Product, ProductSize, quantity, originalPrice, unitPrice 주입)
    public CartItem(Product product, ProductSize productSize, Integer quantity, BigDecimal originalPrice, BigDecimal price) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        if (originalPrice.compareTo(BigDecimal.ZERO) < 0 || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }

        this.product = product;
        this.productSize = productSize;
        this.originalPrice = originalPrice;
        this.price = price;
        this.selected = true;
        this.quantity = quantity;
        this.discountAmount = originalPrice.subtract(price);

        setThumbnailFromProduct(product);
    }

    private void setThumbnailFromProduct(Product product)
    {
        if (product.getMainImageUrls() != null && !product.getMainImageUrls().isEmpty()) {
            this.thumbnailImageUrl = product.getMainImageUrls().get(0).getImageUrl();
        }
    }

    // 수량 변경
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        this.quantity = newQuantity;
    }

    // 선택
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    // price * quantity (총 가격)
    public BigDecimal getTotalPrice() {
        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // 재고 확인
    public boolean isStockAvailable() {
        if (productSize == null || quantity == null) {
            return false;
        }
        return productSize.getStock() >= quantity;
    }
}
