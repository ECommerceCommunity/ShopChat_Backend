package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;
import com.cMall.feedShop.product.application.dto.response.ProductDetailResponse;
import com.cMall.feedShop.product.application.dto.response.ProductPageResponse;
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
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
@DisplayName("상품 조회 서비스 테스트")
public class ProductReadServiceTest {
    @Mock private ProductRepository productRepository;
    @Mock private DiscountCalculator discountCalculator;

    @InjectMocks
    private ProductReadService productReadService;

    private Product product;
    private Store store;
    private Category category;

    @BeforeEach
    public void setUp() {
        // 스토어 생성
        store = Store.builder().storeName("테스트 스토어").sellerId(1L).build();
        ReflectionTestUtils.setField(store, "storeId", 1L);

        // 카테고리 생성
        category = new Category(CategoryType.SNEAKERS, "운동화");
        ReflectionTestUtils.setField(category, "categoryId", 1L);

        // 상품 생성
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
    @DisplayName("상품 목록 조회 성공")
    void getProductList_Success() {
        // Given
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1);
        given(productRepository.findByDeletedAtIsNullOrderByCreatedAtDesc(any()))
                .willReturn(productPage);
        given(discountCalculator.calculateDiscountPrice(any(), any(), any()))
                .willReturn(new BigDecimal("45000"));

        // When
        ProductPageResponse response = productReadService.getProductList(0, 20);

        // Then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getName()).isEqualTo("테스트 상품");
        assertThat(response.getTotalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("상품 상세 조회 성공")
    void getProductDetail_Success() {
        // Given
        given(productRepository.findByProductIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(product));
        given(discountCalculator.calculateDiscountPrice(any(), any(), any()))
                .willReturn(new BigDecimal("45000"));

        try (MockedStatic<Hibernate> hibernateMock = mockStatic(Hibernate.class)) {
            hibernateMock.when(() -> Hibernate.initialize(any())).then(invocation -> null);

            // When
            ProductDetailResponse response = productReadService.getProductDetail(1L);

            // Then
            assertThat(response.getProductId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("테스트 상품");
            assertThat(response.getStoreName()).isEqualTo("테스트 스토어");
            assertThat(response.getCategoryName()).isEqualTo("운동화");

            hibernateMock.verify(() -> Hibernate.initialize(product.getProductOptions()));
        }
    }

    @Test
    @DisplayName("상품 상세 조회 실패")
    void getProductDetail_Fail_ProductNotFound() {
        // Given
        given(productRepository.findByProductIdAndDeletedAtIsNull(999L))
                .willReturn(Optional.empty());

        // When & Then
        BusinessException thrown = assertThrows(BusinessException.class,
                () -> productReadService.getProductDetail(999L));

        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
    }
}
