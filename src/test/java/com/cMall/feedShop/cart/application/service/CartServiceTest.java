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
import com.cMall.feedShop.product.domain.enums.Color;
import com.cMall.feedShop.product.domain.enums.Gender;
import com.cMall.feedShop.product.domain.enums.ImageType;
import com.cMall.feedShop.product.domain.enums.Size;
import com.cMall.feedShop.product.domain.model.ProductImage;
import com.cMall.feedShop.product.domain.model.ProductOption;
import com.cMall.feedShop.product.domain.repository.ProductImageRepository;
import com.cMall.feedShop.product.domain.repository.ProductOptionRepository;
import com.cMall.feedShop.user.domain.enums.UserRole;
import com.cMall.feedShop.user.domain.model.User;
import com.cMall.feedShop.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService 테스트")
class CartServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ProductOptionRepository productOptionRepository;
    @Mock private ProductImageRepository productImageRepository;
    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private UserDetails userDetails;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Cart cart;
    private ProductOption productOption;
    private ProductImage productImage;
    private CartItemCreateRequest request;

    @BeforeEach
    void setUp() {
        setupUser();
        setupCart();
        setupProductOption();
        setupProductImage();
        setupRequest();
    }

    private void setupUser() {
        user = new User("testUser", "password", "test@test.com", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    private void setupCart() {
        cart = Cart.builder()
                .user(user)
                .build();
        ReflectionTestUtils.setField(cart, "cartId", 1L);
        // user.setCart(cart);
    }

    private void setupProductOption() {
        productOption = new ProductOption(Gender.UNISEX, Size.SIZE_250, Color.WHITE, 100, null);
        ReflectionTestUtils.setField(productOption, "optionId", 1L);
    }

    private void setupProductImage() {
        productImage = new ProductImage("http://test.jpg", ImageType.MAIN, null);
        ReflectionTestUtils.setField(productImage, "imageId", 1L);
    }

    private void setupRequest() {
        request = new CartItemCreateRequest();
        ReflectionTestUtils.setField(request, "optionId", 1L);
        ReflectionTestUtils.setField(request, "imageId", 1L);
        ReflectionTestUtils.setField(request, "quantity", 2);
    }

    @Test
    @DisplayName("장바구니에 새 상품 추가 성공")
    void addCartItem_Success_NewItem() {
        // given
        user.setCart(cart);
        given(userDetails.getUsername()).willReturn("test@test.com");
        given(userRepository.findByLoginId("test@test.com")).willReturn(Optional.of(user));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(productOptionRepository.findByOptionId(1L)).willReturn(Optional.of(productOption));
        given(productImageRepository.findByImageId(1L)).willReturn(Optional.of(productImage));
        given(cartItemRepository.findByCartAndOptionIdAndImageId(cart, 1L, 1L)).willReturn(Optional.empty());

        CartItem savedCartItem = CartItem.builder()
                .cart(cart)
                .optionId(1L)
                .imageId(1L)
                .quantity(2)
                .build();
        ReflectionTestUtils.setField(savedCartItem, "cartItemId", 1L);
        ReflectionTestUtils.setField(savedCartItem, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(savedCartItem, "updatedAt", LocalDateTime.now());

        // 🔥 Answer를 사용해서 실제 저장되는 객체의 필드 확인
        given(cartItemRepository.save(any(CartItem.class))).willAnswer(invocation -> {
            CartItem cartItem = invocation.getArgument(0);
            // ID와 시간 필드 설정
            ReflectionTestUtils.setField(cartItem, "cartItemId", 1L);
            ReflectionTestUtils.setField(cartItem, "createdAt", LocalDateTime.now());
            ReflectionTestUtils.setField(cartItem, "updatedAt", LocalDateTime.now());

            // 디버깅 출력
            System.out.println("Saved CartItem ID: " + cartItem.getCartItemId());
            System.out.println("Saved CartItem Cart: " + cartItem.getCart());
            System.out.println("Saved CartItem Cart ID: " + (cartItem.getCart() != null ? cartItem.getCart().getCartId() : "null"));

            return cartItem;
        });

        // when
        CartItemResponse response = cartService.addCartItem(request, userDetails);

        // 디버깅 출력
        System.out.println("Response: " + response);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCartItemId()).isEqualTo(1L);
        assertThat(response.getQuantity()).isEqualTo(2);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("장바구니 추가 실패 - 사용자 없음")
    void addCartItem_Fail_UserNotFound() {
        // given
        given(userDetails.getUsername()).willReturn("test@test.com");
        given(userRepository.findByLoginId("test@test.com")).willReturn(Optional.empty());

        // when & then
        BusinessException thrown = assertThrows(BusinessException.class, () ->
                cartService.addCartItem(request, userDetails));

        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("장바구니 추가 실패 - 상품 옵션 없음")
    void addCartItem_Fail_ProductOptionNotFound() {
        // given
        user.setCart(cart);
        given(userDetails.getUsername()).willReturn("test@test.com");
        given(userRepository.findByLoginId("test@test.com")).willReturn(Optional.of(user));
        given(productOptionRepository.findByOptionId(1L)).willReturn(Optional.empty());

        // when & then
        ProductException.ProductOptionNotFoundException thrown = assertThrows(
                ProductException.ProductOptionNotFoundException.class, () ->
                        cartService.addCartItem(request, userDetails));

        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_OPTION_NOT_FOUND);
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("장바구니 추가 실패 - 상품 이미지 없음")
    void addCartItem_Fail_ProductImageNotFound() {
        // given
        user.setCart(cart);
        given(userDetails.getUsername()).willReturn("test@test.com");
        given(userRepository.findByLoginId("test@test.com")).willReturn(Optional.of(user));
        given(productOptionRepository.findByOptionId(1L)).willReturn(Optional.of(productOption));
        given(productImageRepository.findByImageId(1L)).willReturn(Optional.empty());

        // when & then
        ProductException.ProductImageNotFoundException thrown = assertThrows(
                ProductException.ProductImageNotFoundException.class, () ->
                        cartService.addCartItem(request, userDetails));

        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_IMAGE_NOT_FOUND);
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("장바구니 추가 실패 - 재고 부족")
    void addCartItem_Fail_OutOfStock() {
        // given
        user.setCart(cart);
        ProductOption lowStockOption = new ProductOption(Gender.UNISEX, Size.SIZE_250, Color.WHITE, 1, null);
        ReflectionTestUtils.setField(lowStockOption, "optionId", 1L);

        given(userDetails.getUsername()).willReturn("test@test.com");
        given(userRepository.findByLoginId("test@test.com")).willReturn(Optional.of(user));
        given(productOptionRepository.findByOptionId(1L)).willReturn(Optional.of(lowStockOption));
        given(productImageRepository.findByImageId(1L)).willReturn(Optional.of(productImage));

        // when & then
        ProductException.OutOfStockException thrown = assertThrows(
                ProductException.OutOfStockException.class, () ->
                        cartService.addCartItem(request, userDetails));

        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.OUT_OF_STOCK);
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("장바구니 추가 실패 - 재고 없음")
    void addCartItem_Fail_NoStock() {
        // given
        user.setCart(cart);
        ProductOption noStockOption = new ProductOption(Gender.UNISEX, Size.SIZE_250, Color.WHITE, 0, null);
        ReflectionTestUtils.setField(noStockOption, "optionId", 1L);

        given(userDetails.getUsername()).willReturn("test@test.com");
        given(userRepository.findByLoginId("test@test.com")).willReturn(Optional.of(user));
        given(productOptionRepository.findByOptionId(1L)).willReturn(Optional.of(noStockOption));
        given(productImageRepository.findByImageId(1L)).willReturn(Optional.of(productImage));

        // when & then
        ProductException.OutOfStockException thrown = assertThrows(
                ProductException.OutOfStockException.class, () ->
                        cartService.addCartItem(request, userDetails));

        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.OUT_OF_STOCK);
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("장바구니가 없는 사용자 - 새 장바구니 생성")
    void addCartItem_Success_CreateNewCart() {
        // given
        User userWithoutCart = new User("testUser", "password", "test@test.com", UserRole.USER);
        ReflectionTestUtils.setField(userWithoutCart, "id", 1L);
        userWithoutCart.setCart(null);

        Cart newCart = Cart.builder()
                .user(userWithoutCart)
                .build();
        ReflectionTestUtils.setField(newCart, "cartId", 2L);

        given(userDetails.getUsername()).willReturn("test@test.com");
        given(userRepository.findByLoginId("test@test.com")).willReturn(Optional.of(userWithoutCart));
        given(userRepository.findById(1L)).willReturn(Optional.of(userWithoutCart));
        given(productOptionRepository.findByOptionId(1L)).willReturn(Optional.of(productOption));
        given(productImageRepository.findByImageId(1L)).willReturn(Optional.of(productImage));
        given(cartRepository.save(any(Cart.class))).willReturn(newCart);
        given(cartItemRepository.findByCartAndOptionIdAndImageId(any(Cart.class), eq(1L), eq(1L)))
                .willReturn(Optional.empty());

        CartItem savedCartItem = CartItem.builder()
                .cart(newCart)
                .optionId(1L)
                .imageId(1L)
                .quantity(2)
                .build();
        ReflectionTestUtils.setField(savedCartItem, "cartItemId", 1L);
        given(cartItemRepository.save(any(CartItem.class))).willReturn(savedCartItem);

        // when
        CartItemResponse response = cartService.addCartItem(request, userDetails);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCartId()).isEqualTo(2L);
        assertThat(response.getQuantity()).isEqualTo(2);
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }
}