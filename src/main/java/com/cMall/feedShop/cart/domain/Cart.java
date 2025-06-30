package com.cMall.feedShop.cart.domain;

import com.cMall.feedShop.user.domain.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 생성자 (User 주입)
    public Cart(User user) {
        this.user = user;
    }

    // 비즈니스 메서드 (CartItem 추가)
    public void addCartItem(CartItem cartItem) {
        if (cartItem == null) {
            throw new IllegalArgumentException("cartItem cannot be null.");
        }
        cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    // 비즈니스 메서드 (CartItem 삭제)
    public void removeCartItem(CartItem cartItem) {
        if (cartItem == null) {
            return;
        }
        cartItems.remove(cartItem);
        cartItem.setCart(null);
    }

    // 장바구니 총 아이템 수 계산
    public int getTotalItemCount() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    // 장바구니 총 가격 계산 (선택된 아이템만)
    public BigDecimal getTotalPrice() {
        return cartItems.stream()
                .filter(CartItem::getSelected)
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 특정 상품 + 사이즈의 장바구니 아이템 찾기
    public CartItem findCartItemByProductAndSize(Long productId, Long productSizeId) {
        return cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId) && item.getProductSize().getId().equals(productSizeId))
                .findFirst()
                .orElse(null);
    }

    // 장바구니 비우기
    public void clearCart() {
        cartItems.clear();
    }
}
