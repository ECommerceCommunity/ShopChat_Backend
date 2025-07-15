package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.product.application.dto.response.ProductDetailResponse;
import com.cMall.feedShop.product.application.dto.response.ProductPageResponse;
import com.cMall.feedShop.product.application.exception.ProductException;
import com.cMall.feedShop.product.application.util.DiscountCalculator;
import com.cMall.feedShop.product.domain.enums.CategoryType;
import com.cMall.feedShop.product.domain.model.Category;
import com.cMall.feedShop.product.domain.model.Product;
import com.cMall.feedShop.product.domain.repository.ProductRepository;
import com.cMall.feedShop.store.domain.model.Store;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductReadService 테스트")
class ProductReadServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private DiscountCalculator discountCalculator;

    @InjectMocks
    private ProductReadService productReadService;

    private Product product;
    private Store store;
    private Category category;

    @BeforeEach
    void setUp() {
        store = Store.builder()
                .storeName("테스트 스토어")
                .sellerId(1L)
                .build();
        ReflectionTestUtils.setField(store, "storeId", 1L);

        category = new Category(CategoryType.SNEAKERS, "운동화");
        ReflectionTestUtils.setField(category, "categoryId", 1L);

        product = Product.builder()
                .name("테스트 상품")
                .price(new BigDecimal("50000"))
                .store(store)
                .category(category)
                .description("테스트 상품입니다.")
                .build();
        ReflectionTestUtils.setField(product, "productId", 1L);
        ReflectionTestUtils.setField(product, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(product, "updatedAt", LocalDateTime.now());
        ReflectionTestUtils.setField(product, "productImages", List.of());
        ReflectionTestUtils.setField(product, "productOptions", List.of());
    }

    @Test
    @DisplayName("상품이 존재할때_getProductList 호출하면_상품 목록이 반환된다")
    void givenProductsExist_whenGetProductList_thenReturnProductList() {
        // given
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1);
        given(productRepository.findAllByOrderByCreatedAtDesc(any()))
                .willReturn(productPage);
        given(discountCalculator.calculateDiscountPrice(any(), any(), any()))
                .willReturn(new BigDecimal("45000"));

        // when
        ProductPageResponse result = productReadService.getProductList(0, 20);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("테스트 상품");
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(20);
        assertThat(result.getNumber()).isEqualTo(0);
        verify(productRepository, times(1)).findAllByOrderByCreatedAtDesc(any());
    }

    @Test
    @DisplayName("상품이 존재하지않을때_getProductList 호출하면_빈 목록이 반환된다")
    void givenNoProductsExist_whenGetProductList_thenReturnEmptyList() {
        // given
        Page<Product> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        given(productRepository.findAllByOrderByCreatedAtDesc(any()))
                .willReturn(emptyPage);

        // when
        ProductPageResponse result = productReadService.getProductList(0, 20);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0L);
    }

    @Test
    @DisplayName("음수 페이지가 주어졌을때_getProductList 호출하면_0페이지로 조정된다")
    void givenNegativePage_whenGetProductList_thenPageAdjustedToZero() {
        // given
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1);
        given(productRepository.findAllByOrderByCreatedAtDesc(any()))
                .willReturn(productPage);
        given(discountCalculator.calculateDiscountPrice(any(), any(), any()))
                .willReturn(new BigDecimal("45000"));

        // when
        ProductPageResponse result = productReadService.getProductList(-1, 20);

        // then
        assertThat(result.getNumber()).isEqualTo(0);
        verify(productRepository, times(1)).findAllByOrderByCreatedAtDesc(
                PageRequest.of(0, 20));
    }

    @Test
    @DisplayName("잘못된 사이즈가 주어졌을때_getProductList 호출하면_기본값 20으로 조정된다")
    void givenInvalidSize_whenGetProductList_thenSizeAdjustedToDefault() {
        // given
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1);
        given(productRepository.findAllByOrderByCreatedAtDesc(any()))
                .willReturn(productPage);
        given(discountCalculator.calculateDiscountPrice(any(), any(), any()))
                .willReturn(new BigDecimal("45000"));

        // when
        ProductPageResponse result = productReadService.getProductList(0, 200);

        // then
        assertThat(result.getSize()).isEqualTo(20);
        verify(productRepository, times(1)).findAllByOrderByCreatedAtDesc(
                PageRequest.of(0, 20));
    }

    @Test
    @DisplayName("유효한 상품ID가 주어졌을때_getProductDetail 호출하면_상품 상세정보가 반환된다")
    void givenValidProductId_whenGetProductDetail_thenReturnProductDetail() {
        // given
        given(productRepository.findByProductId(1L))
                .willReturn(Optional.of(product));
        given(discountCalculator.calculateDiscountPrice(any(), any(), any()))
                .willReturn(new BigDecimal("45000"));

        try (MockedStatic<Hibernate> hibernateMock = mockStatic(Hibernate.class)) {
            hibernateMock.when(() -> Hibernate.initialize(any())).then(invocation -> null);

            // when
            ProductDetailResponse result = productReadService.getProductDetail(1L);

            // then
            assertThat(result.getProductId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("테스트 상품");
            assertThat(result.getStoreName()).isEqualTo("테스트 스토어");
            assertThat(result.getCategoryName()).isEqualTo("운동화");
            assertThat(result.getDescription()).isEqualTo("테스트 상품입니다.");
            hibernateMock.verify(() -> Hibernate.initialize(product.getProductOptions()));
        }
    }

    @Test
    @DisplayName("존재하지않는 상품ID가 주어졌을때_getProductDetail 호출하면_상품 없음 예외가 발생한다")
    void givenNonExistentProductId_whenGetProductDetail_thenThrowsProductNotFoundException() {
        // given
        given(productRepository.findByProductId(999L))
                .willReturn(Optional.empty());

        // when & then
        ProductException.ProductNotFoundException thrown = assertThrows(
                ProductException.ProductNotFoundException.class,
                () -> productReadService.getProductDetail(999L));

        assertThat(thrown.getErrorCode().getMessage()).contains("상품을 찾을 수 없습니다");
        verify(productRepository, times(1)).findByProductId(999L);
    }

    @Test
    @DisplayName("삭제된 상품ID가 주어졌을때_getProductDetail 호출하면_상품 없음 예외가 발생한다")
    void givenDeletedProductId_whenGetProductDetail_thenThrowsProductNotFoundException() {
        // given
        given(productRepository.findByProductId(1L))
                .willReturn(Optional.empty());

        // when & then
        ProductException.ProductNotFoundException thrown = assertThrows(
                ProductException.ProductNotFoundException.class,
                () -> productReadService.getProductDetail(1L));

        assertThat(thrown.getErrorCode().getMessage()).contains("상품을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("큰 페이지 사이즈가 주어졌을때_getProductList 호출하면_최대값으로 제한된다")
    void givenLargePageSize_whenGetProductList_thenSizeLimitedToMaximum() {
        // given
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1);
        given(productRepository.findAllByOrderByCreatedAtDesc(any()))
                .willReturn(productPage);
        given(discountCalculator.calculateDiscountPrice(any(), any(), any()))
                .willReturn(new BigDecimal("45000"));

        // when
        ProductPageResponse result = productReadService.getProductList(0, 150);

        // then
        verify(productRepository, times(1)).findAllByOrderByCreatedAtDesc(
                PageRequest.of(0, 20)); // 최대 100을 넘으면 기본값 20으로 설정
    }
}