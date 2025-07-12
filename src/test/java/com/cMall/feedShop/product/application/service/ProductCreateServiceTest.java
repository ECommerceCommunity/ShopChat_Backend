package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;
import com.cMall.feedShop.product.application.dto.request.ProductCreateRequest;
import com.cMall.feedShop.product.application.dto.request.ProductImageRequest;
import com.cMall.feedShop.product.application.dto.request.ProductOptionRequest;
import com.cMall.feedShop.product.application.dto.response.ProductCreateResponse;
import com.cMall.feedShop.product.domain.enums.*;
import com.cMall.feedShop.product.domain.model.Product;
import com.cMall.feedShop.product.domain.repository.CategoryRepository;
import com.cMall.feedShop.product.domain.repository.ProductRepository;
import com.cMall.feedShop.product.domain.model.Category;
import com.cMall.feedShop.store.domain.model.Store;
import com.cMall.feedShop.store.domain.repository.StoreRepository;
import com.cMall.feedShop.user.domain.enums.UserRole;
import com.cMall.feedShop.user.domain.model.User;
import com.cMall.feedShop.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("상품 등록 서비스 테스트")
public class ProductCreateServiceTest {
    @Mock private ProductRepository productRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private ProductCreateService productCreateService;

    private ProductCreateRequest request;
    private User seller;
    private Store store;
    private Category category;

    @BeforeEach
    public void setUp() {
        // 판매자 생성
        seller = new User("seller123", "password", "seller@test.com", UserRole.SELLER);
        seller.setId(2L);

        // 스토어 생성
        store = Store.builder().storeName("테스트 스토어").sellerId(2L).build();
        ReflectionTestUtils.setField(store, "storeId", 1L);

        // 카테고리 생성
        category = new Category(CategoryType.SNEAKERS, "운동화");
        ReflectionTestUtils.setField(category, "categoryId", 1L);

        // 요청 데이터 생성
        request = new ProductCreateRequest();
        ReflectionTestUtils.setField(request, "name", "테스트 상품");
        ReflectionTestUtils.setField(request, "price", new BigDecimal("50000"));
        ReflectionTestUtils.setField(request, "categoryId", 1L);
        ReflectionTestUtils.setField(request, "description", "테스트 상품입니다.");

        // 이미지 생성
        ProductImageRequest imageRequest = new ProductImageRequest();
        ReflectionTestUtils.setField(imageRequest, "url", "https://test.jpg");
        ReflectionTestUtils.setField(imageRequest, "type", ImageType.MAIN);
        ReflectionTestUtils.setField(request, "images", List.of(imageRequest));

        // 옵션 생성
        ProductOptionRequest optionRequest = new ProductOptionRequest();
        ReflectionTestUtils.setField(optionRequest, "gender", Gender.UNISEX);
        ReflectionTestUtils.setField(optionRequest, "size", Size.SIZE_250);
        ReflectionTestUtils.setField(optionRequest, "color", Color.BLACK);
        ReflectionTestUtils.setField(optionRequest, "stock", 100);
        ReflectionTestUtils.setField(request, "options", List.of(optionRequest));
    }

    @Test
    @DisplayName("상품 등록 성공")
    void createProduct_Success() {
        // Given
        Product savedProduct = Product.builder()
                .name("테스트 상품")
                .price(new BigDecimal("50000"))
                .store(store)
                .category(category)
                .build();
        ReflectionTestUtils.setField(savedProduct, "productId", 1L);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(true);
            given(authentication.getName()).willReturn("test2");

            given(userRepository.findByLoginId("test2")).willReturn(Optional.of(seller));
            given(userRepository.findById(2L)).willReturn(Optional.of(seller));
            given(storeRepository.findBySellerId(2L)).willReturn(Optional.of(store));
            given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
            given(productRepository.save(any(Product.class))).willReturn(savedProduct);

            // When
            ProductCreateResponse response = productCreateService.createProduct(request);

            // Then
            assertThat(response.getProductId()).isEqualTo(1L);
            verify(productRepository, times(1)).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("상품 등록 실패 - 판매자가 아닌 사용자")
    void createProduct_Fail_NotSeller() {
        // Given
        User normalUser = new User("user123", "password", "user@test.com", UserRole.USER);
        normalUser.setId(2L);
        given(userRepository.findById(2L)).willReturn(Optional.of(normalUser));

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(true);
            given(authentication.getName()).willReturn("test2");

            given(userRepository.findByLoginId("test2")).willReturn(Optional.of(normalUser));

            // When & Then
            BusinessException thrown = assertThrows(BusinessException.class, () ->
                    productCreateService.createProduct(request));

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
        }
    }
}
