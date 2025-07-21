package com.cMall.feedShop.cart.application.service;

import com.cMall.feedShop.cart.application.dto.request.CartItemCreateRequest;
import com.cMall.feedShop.cart.application.dto.response.CartItemResponse;
import com.cMall.feedShop.cart.domain.model.Cart;
import com.cMall.feedShop.cart.domain.model.CartItem;
import com.cMall.feedShop.cart.domain.repository.CartItemRepository;
import com.cMall.feedShop.cart.domain.repository.CartRepository;
import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;
import com.cMall.feedShop.product.application.exception.ProductException;
import com.cMall.feedShop.product.domain.model.ProductImage;
import com.cMall.feedShop.product.domain.model.ProductOption;
import com.cMall.feedShop.product.domain.repository.ProductImageRepository;
import com.cMall.feedShop.product.domain.repository.ProductOptionRepository;
import com.cMall.feedShop.user.domain.model.User;
import com.cMall.feedShop.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final UserRepository userRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductImageRepository productImageRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartItemResponse addCartItem(CartItemCreateRequest request, UserDetails userDetails) {
        // 1. 현재 사용자 ID 가져오기
        Long currentUserId = getCurrentUserId(userDetails);

        // 2. 상품 옵션 검증
        ProductOption productOption = validateProductOption(request.getOptionId());

        // 3. 상품 이미지 검증
        validateProductImage(request.getImageId());

        // 4. 재고 확인
        validateStock(productOption, request.getQuantity());

        // 5. 장바구니 조회 또는 생성
        Cart cart = getOrCreateCart(currentUserId);

        // 6. 해당 장바구니의 아이템에서 cart와 optionId와 imageId로 이미 저장된 같은 아이템이 있는지 조회
        Optional<CartItem> existingCartItem = cartItemRepository
                .findByCartAndOptionIdAndImageId(cart, request.getOptionId(), request.getImageId());

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            // 7. 기존 장바구니 아이템이 있으면 해당 아이템의 수량을 업데이트
            cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            // 증가된 수량으로 재고 재확인
            validateStock(productOption, newQuantity);

            // 수량 업데이트
            cartItem.updateQuantity(newQuantity);
        } else {
            // 8. 기존 장바구니 아이템이 없으면 새로운 장바구니 아이템 생성
            cartItem = CartItem.builder()
                    .cart(cart)
                    .optionId(request.getOptionId())
                    .imageId(request.getImageId())
                    .quantity(request.getQuantity())
                    .build();
        }

        // 9. DB에 저장
        cartItemRepository.save(cartItem);

        // 10. 응답값 리턴
        return CartItemResponse.from(cartItem);
    }

    // JWT 에서 현재 사용자 ID 추출
    private Long getCurrentUserId(UserDetails userDetails) {
        String login_id = userDetails.getUsername();
        User user = userRepository.findByLoginId(login_id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return user.getId();
    }

    private ProductOption validateProductOption(Long optionId) {
        // ProductOption을 찾는다.
        ProductOption productOption = productOptionRepository.findByOptionId(optionId)
                .orElseThrow(() -> new ProductException.ProductOptionNotFoundException());

        return productOption;
    }

    private ProductImage validateProductImage(Long imageId) {
        // ProductImage를 찾는다.
        ProductImage productImage = productImageRepository.findByImageId(imageId)
                .orElseThrow(() -> new ProductException.ProductImageNotFoundException());
        return productImage;
    }

    private void validateStock(ProductOption productOption, Integer quantity) {
        // 재고가 충분한지 확인한다.
        if (!productOption.isInStock() || productOption.getStock() < quantity) {
            throw new ProductException.OutOfStockException();
        }
    }

    private Cart getOrCreateCart(Long currentUserId) {
        // user를 찾는다.
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // user의 cart가 있으면 해당 cart를 반환
        if (user.getCart() != null) {
            return user.getCart();
        }

        // user의 cart가 없으면 새로 생성
        Cart newCart = Cart.builder()
                .user(user)
                .build();

        // 새로 생성한 cart를 DB에 저장
        Cart savedCart = cartRepository.save(newCart);
        user.setCart(savedCart);

        return savedCart;
    }
}
