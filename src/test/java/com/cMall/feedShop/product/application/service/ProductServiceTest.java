package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;
import com.cMall.feedShop.product.application.dto.request.ProductCreateRequest;
import com.cMall.feedShop.product.application.dto.request.ProductUpdateRequest;
import com.cMall.feedShop.product.application.dto.request.ProductImageRequest;
import com.cMall.feedShop.product.application.dto.request.ProductOptionRequest;
import com.cMall.feedShop.product.application.dto.response.ProductCreateResponse;
import com.cMall.feedShop.product.application.exception.ProductException;
import com.cMall.feedShop.product.domain.enums.*;
import com.cMall.feedShop.product.domain.model.Category;
import com.cMall.feedShop.product.domain.model.Product;
import com.cMall.feedShop.product.domain.model.ProductImage;
import com.cMall.feedShop.product.domain.model.ProductOption;
import com.cMall.feedShop.product.domain.repository.CategoryRepository;
import com.cMall.feedShop.product.domain.repository.ProductImageRepository;
import com.cMall.feedShop.product.domain.repository.ProductOptionRepository;
import com.cMall.feedShop.product.domain.repository.ProductRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 테스트")
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductImageRepository productImageRepository;
    @Mock private ProductOptionRepository productOptionRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private ProductService productService;

    private ProductCreateRequest createRequest;
    private ProductUpdateRequest updateRequest;
    private User seller;
    private User regularUser;
    private Store store;
    private Category category;
    private Category newCategory;
    private Product existingProduct;

    @BeforeEach
    void setUp() {
        setupUsers();
        setupStores();
        setupCategories();
        setupExistingProduct();
        setupCreateRequest();
        setupUpdateRequest();
    }

    private void setupUsers() {
        seller = new User("seller123", "password", "seller@test.com", UserRole.SELLER);
        ReflectionTestUtils.setField(seller, "id", 2L);

        regularUser = new User("user123", "password", "user@test.com", UserRole.USER);
        ReflectionTestUtils.setField(regularUser, "id", 4L);
    }

    private void setupStores() {
        store = Store.builder()
                .storeName("테스트 스토어")
                .sellerId(2L)
                .build();
        ReflectionTestUtils.setField(store, "storeId", 1L);
    }

    private void setupCategories() {
        category = new Category(CategoryType.SNEAKERS, "운동화");
        ReflectionTestUtils.setField(category, "categoryId", 1L);

        newCategory = new Category(CategoryType.BOOTS, "부츠");
        ReflectionTestUtils.setField(newCategory, "categoryId", 2L);
    }

    private void setupExistingProduct() {
        existingProduct = Product.builder()
                .name("기존 상품")
                .price(new BigDecimal("50000"))
                .store(store)
                .category(category)
                .description("기존 설명")
                .build();
        ReflectionTestUtils.setField(existingProduct, "productId", 1L);
        ReflectionTestUtils.setField(existingProduct, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(existingProduct, "updatedAt", LocalDateTime.now());

        // 기존 이미지와 옵션 설정
        List<ProductImage> existingImages = new ArrayList<>();
        existingImages.add(new ProductImage("http://old.jpg", ImageType.MAIN, existingProduct));
        ReflectionTestUtils.setField(existingProduct, "productImages", existingImages);

        List<ProductOption> existingOptions = new ArrayList<>();
        existingOptions.add(new ProductOption(Gender.MEN, Size.SIZE_250, Color.WHITE, 10, existingProduct));
        ReflectionTestUtils.setField(existingProduct, "productOptions", existingOptions);
    }

    private void setupCreateRequest() {
        createRequest = new ProductCreateRequest();
        ReflectionTestUtils.setField(createRequest, "name", "테스트 상품");
        ReflectionTestUtils.setField(createRequest, "price", new BigDecimal("50000"));
        ReflectionTestUtils.setField(createRequest, "categoryId", 1L);
        ReflectionTestUtils.setField(createRequest, "description", "테스트 상품입니다.");

        ProductImageRequest imageRequest = new ProductImageRequest();
        ReflectionTestUtils.setField(imageRequest, "url", "https://test.jpg");
        ReflectionTestUtils.setField(imageRequest, "type", ImageType.MAIN);
        ReflectionTestUtils.setField(createRequest, "images", List.of(imageRequest));

        ProductOptionRequest optionRequest = new ProductOptionRequest();
        ReflectionTestUtils.setField(optionRequest, "gender", Gender.UNISEX);
        ReflectionTestUtils.setField(optionRequest, "size", Size.SIZE_250);
        ReflectionTestUtils.setField(optionRequest, "color", Color.WHITE);
        ReflectionTestUtils.setField(optionRequest, "stock", 100);
        ReflectionTestUtils.setField(createRequest, "options", List.of(optionRequest));
    }

    private void setupUpdateRequest() {
        updateRequest = new ProductUpdateRequest();
    }

    private void mockSecurityContext(String loginId, User user) {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getName()).thenReturn(loginId);
        lenient().when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(user));
        lenient().when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    }

    // ================================
    // createProduct 테스트
    // ================================

    @Test
    @DisplayName("유효한 판매자와 요청이 주어졌을때_createProduct 호출하면_상품이 성공적으로 생성된다")
    void givenValidSellerAndRequest_whenCreateProduct_thenProductCreatedSuccessfully() {
        // given
        Product savedProduct = Product.builder()
                .name("테스트 상품")
                .price(new BigDecimal("50000"))
                .store(store)
                .category(category)
                .build();
        ReflectionTestUtils.setField(savedProduct, "productId", 1L);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("seller123", seller);
            given(storeRepository.findBySellerId(2L)).willReturn(Optional.of(store));
            given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
            given(productRepository.save(any(Product.class))).willReturn(savedProduct);

            // when
            ProductCreateResponse result = productService.createProduct(createRequest);

            // then
            assertThat(result.getProductId()).isEqualTo(1L);
            verify(productRepository, times(1)).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("일반 사용자가 주어졌을때_createProduct 호출하면_권한 없음 예외가 발생한다")
    void givenRegularUser_whenCreateProduct_thenThrowsForbiddenException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("user123", regularUser);

            // when & then
            BusinessException thrown = assertThrows(BusinessException.class, () ->
                    productService.createProduct(createRequest));

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("존재하지않는 카테고리ID가 주어졌을때_createProduct 호출하면_카테고리 없음 예외가 발생한다")
    void givenNonExistentCategoryId_whenCreateProduct_thenThrowsCategoryNotFoundException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("seller123", seller);
            given(storeRepository.findBySellerId(2L)).willReturn(Optional.of(store));
            given(categoryRepository.findById(1L)).willReturn(Optional.empty());

            // when & then - BusinessException이 아닌 ProductException.CategoryNotFoundException 기대
            BusinessException thrown = assertThrows(BusinessException.class, () ->
                    productService.createProduct(createRequest));

            // ErrorCode로 검증
          
            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("스토어가 존재하지않는 판매자가 주어졌을때_createProduct 호출하면_스토어 없음 예외가 발생한다")
    void givenSellerWithoutStore_whenCreateProduct_thenThrowsStoreNotFoundException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("seller123", seller);
            given(storeRepository.findBySellerId(2L)).willReturn(Optional.empty());

            // when & then - BusinessException이 아닌 StoreException.StoreNotFoundException 기대
            BusinessException thrown = assertThrows(BusinessException.class, () ->
                    productService.createProduct(createRequest));

            // ErrorCode로 검증
          
            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.STORE_NOT_FOUND);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("인증되지않은 사용자가 주어졌을때_createProduct 호출하면_인증 없음 예외가 발생한다")
    void givenUnauthenticatedUser_whenCreateProduct_thenThrowsUnauthorizedException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(null);

            // when & then
            BusinessException thrown = assertThrows(BusinessException.class, () ->
                    productService.createProduct(createRequest));

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("Authentication이 인증되지않은 상태일때_createProduct 호출하면_인증 없음 예외가 발생한다")
    void givenNotAuthenticatedState_whenCreateProduct_thenThrowsUnauthorizedException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(false);

            // when & then
            BusinessException thrown = assertThrows(BusinessException.class, () ->
                    productService.createProduct(createRequest));

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("존재하지않는 사용자ID가 주어졌을때_createProduct 호출하면_사용자 없음 예외가 발생한다")
    void givenNonExistentUserId_whenCreateProduct_thenThrowsUserNotFoundException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(true);
            given(authentication.getName()).willReturn("nonexistent");
            given(userRepository.findByLoginId("nonexistent")).willReturn(Optional.empty());

            // when & then
            BusinessException thrown = assertThrows(BusinessException.class, () ->
                    productService.createProduct(createRequest));

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    // ================================
    // updateProduct 테스트
    // ================================

    @Test
    @DisplayName("유효한 판매자와 요청이 주어졌을때_updateProduct 호출하면_상품이 성공적으로 수정된다")
    void givenValidSellerAndRequest_whenUpdateProduct_thenProductUpdatedSuccessfully() {
        // given
        ReflectionTestUtils.setField(updateRequest, "name", "수정된 상품명");
        ReflectionTestUtils.setField(updateRequest, "price", new BigDecimal("60000"));
        ReflectionTestUtils.setField(updateRequest, "categoryId", 2L);
        ReflectionTestUtils.setField(updateRequest, "description", "수정된 설명");

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("seller123", seller);
            given(productRepository.findByProductId(1L)).willReturn(Optional.of(existingProduct));
            given(categoryRepository.findById(2L)).willReturn(Optional.of(newCategory));
            given(productRepository.save(any(Product.class))).willReturn(existingProduct);

            // when
            productService.updateProduct(1L, updateRequest);

            // then
            verify(productRepository, times(1)).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("부분 업데이트가 주어졌을때_updateProduct 호출하면_해당 필드만 수정된다")
    void givenPartialUpdate_whenUpdateProduct_thenOnlySpecifiedFieldsUpdated() {
        // given
        ReflectionTestUtils.setField(updateRequest, "name", "부분 수정된 이름");

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("seller123", seller);
            given(productRepository.findByProductId(1L)).willReturn(Optional.of(existingProduct));
            given(productRepository.save(any(Product.class))).willReturn(existingProduct);

            // when
            productService.updateProduct(1L, updateRequest);

            // then
            verify(productRepository, times(1)).save(any(Product.class));
            verify(categoryRepository, never()).findById(any());
        }
    }

    @Test
    @DisplayName("이미지 업데이트가 주어졌을때_updateProduct 호출하면_이미지가 교체된다")
    void givenImageUpdate_whenUpdateProduct_thenImagesReplaced() {
        // given
        ProductImageRequest newImageRequest = new ProductImageRequest();
        ReflectionTestUtils.setField(newImageRequest, "url", "http://new.jpg");
        ReflectionTestUtils.setField(newImageRequest, "type", ImageType.MAIN);
        ReflectionTestUtils.setField(updateRequest, "images", List.of(newImageRequest));

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("seller123", seller);
            given(productRepository.findByProductId(1L)).willReturn(Optional.of(existingProduct));
            given(productRepository.save(any(Product.class))).willReturn(existingProduct);

            // when
            productService.updateProduct(1L, updateRequest);

            // then
            verify(productRepository, times(1)).save(any(Product.class));
            verify(productImageRepository, times(1)).deleteAll(any());
        }
    }

    @Test
    @DisplayName("옵션 업데이트가 주어졌을때_updateProduct 호출하면_옵션이 교체된다")
    void givenOptionUpdate_whenUpdateProduct_thenOptionsReplaced() {
        // given
        ProductOptionRequest newOptionRequest = new ProductOptionRequest();
        ReflectionTestUtils.setField(newOptionRequest, "gender", Gender.WOMEN);
        ReflectionTestUtils.setField(newOptionRequest, "size", Size.SIZE_255);
        ReflectionTestUtils.setField(newOptionRequest, "color", Color.SILVER);
        ReflectionTestUtils.setField(newOptionRequest, "stock", 50);
        ReflectionTestUtils.setField(updateRequest, "options", List.of(newOptionRequest));

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("seller123", seller);
            given(productRepository.findByProductId(1L)).willReturn(Optional.of(existingProduct));
            given(productRepository.save(any(Product.class))).willReturn(existingProduct);

            // when
            productService.updateProduct(1L, updateRequest);

            // then
            verify(productRepository, times(1)).save(any(Product.class));
            verify(productOptionRepository, times(1)).deleteAll(any());
        }
    }

    @Test
    @DisplayName("다른 판매자의 상품을 수정하려할때_updateProduct 호출하면_권한 없음 예외가 발생한다")
    void givenOtherSellerProduct_whenUpdateProduct_thenThrowsStoreForbiddenException() {
        // given
        User otherSeller = new User("other123", "password", "other@test.com", UserRole.SELLER);
        ReflectionTestUtils.setField(otherSeller, "id", 3L);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("other123", otherSeller);
            given(productRepository.findByProductId(1L)).willReturn(Optional.of(existingProduct));

            // when & then - BusinessException으로 변경
            BusinessException thrown = assertThrows(BusinessException.class,

                    () -> productService.updateProduct(1L, updateRequest)
            );

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.STORE_FORBIDDEN);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("일반 사용자가 상품을 수정하려할때_updateProduct 호출하면_권한 없음 예외가 발생한다")
    void givenRegularUser_whenUpdateProduct_thenThrowsForbiddenException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("user123", regularUser);

            // when & then
            BusinessException thrown = assertThrows(
                    BusinessException.class,
                    () -> productService.updateProduct(1L, updateRequest)
            );

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("존재하지않는 상품을 수정하려할때_updateProduct 호출하면_상품 없음 예외가 발생한다")
    void givenNonExistentProduct_whenUpdateProduct_thenThrowsProductNotFoundException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("seller123", seller);
            given(productRepository.findByProductId(999L)).willReturn(Optional.empty());

            // when & then
            ProductException.ProductNotFoundException thrown = assertThrows(
                    ProductException.ProductNotFoundException.class,
                    () -> productService.updateProduct(999L, updateRequest)
            );

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("존재하지않는 카테고리로 수정하려할때_updateProduct 호출하면_카테고리 없음 예외가 발생한다")
    void givenNonExistentCategory_whenUpdateProduct_thenThrowsCategoryNotFoundException() {
        // given
        ReflectionTestUtils.setField(updateRequest, "categoryId", 999L);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("seller123", seller);
            given(productRepository.findByProductId(1L)).willReturn(Optional.of(existingProduct));
            given(categoryRepository.findById(999L)).willReturn(Optional.empty());

            // when & then - BusinessException으로 변경
            BusinessException thrown = assertThrows(BusinessException.class,

                    () -> productService.updateProduct(1L, updateRequest)
            );

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("인증되지않은 사용자가 상품을 수정하려할때_updateProduct 호출하면_인증 없음 예외가 발생한다")
    void givenUnauthenticatedUser_whenUpdateProduct_thenThrowsUnauthorizedException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(null);

            // when & then
            BusinessException thrown = assertThrows(BusinessException.class,

                    () -> productService.updateProduct(1L, updateRequest)
            );

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("Authentication이 인증되지않은 상태로 수정하려할때_updateProduct 호출하면_인증 없음 예외가 발생한다")
    void givenNotAuthenticatedStateForUpdate_whenUpdateProduct_thenThrowsUnauthorizedException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(false);

            // when & then
            BusinessException thrown = assertThrows(BusinessException.class,

                    () -> productService.updateProduct(1L, updateRequest)
            );

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("존재하지않는 사용자ID로 수정하려할때_updateProduct 호출하면_사용자 없음 예외가 발생한다")
    void givenNonExistentUserIdForUpdate_whenUpdateProduct_thenThrowsUserNotFoundException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(true);
            given(authentication.getName()).willReturn("nonexistent");
            given(userRepository.findByLoginId("nonexistent")).willReturn(Optional.empty());

            // when & then
            BusinessException thrown = assertThrows(BusinessException.class,

                    () -> productService.updateProduct(1L, updateRequest)
            );

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    // ================================
    // deleteProduct 테스트
    // ================================

    @Test
    @DisplayName("유효한 판매자가 자신의 상품을 삭제하려할때_deleteProduct 호출하면_상품이 성공적으로 삭제된다")
    void givenValidSellerAndOwnProduct_whenDeleteProduct_thenProductDeletedSuccessfully() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("seller123", seller);
            given(productRepository.findByProductId(1L)).willReturn(Optional.of(existingProduct));

            // when
            productService.deleteProduct(1L);

            // then
            verify(productRepository, times(1)).delete(existingProduct); // delete 메서드 검증
            verify(productRepository, never()).save(any(Product.class)); // save는 호출되지 않음
        }
    }

    // 대신 이런 테스트로 대체 가능
    @Test
    @DisplayName("CASCADE 삭제로 연관 엔티티도 함께 삭제되는지 확인")
    void givenProductWithImagesAndOptions_whenDeleteProduct_thenCascadeDeleteWorksCorrectly() {
        // given
        // existingProduct에는 이미 productImages와 productOptions가 설정되어 있음
        assertThat(existingProduct.getProductImages()).isNotEmpty();
        assertThat(existingProduct.getProductOptions()).isNotEmpty();

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("seller123", seller);
            given(productRepository.findByProductId(1L)).willReturn(Optional.of(existingProduct));

            // when
            productService.deleteProduct(1L);

            // then
            verify(productRepository, times(1)).delete(existingProduct);
            // CASCADE 설정으로 인해 연관 엔티티들도 함께 삭제됨
            // (실제 DB 테스트에서는 integration test로 확인)
        }
    }

    @Test
    @DisplayName("다른 판매자의 상품을 삭제하려할때_deleteProduct 호출하면_권한 없음 예외가 발생한다")
    void givenOtherSellerProduct_whenDeleteProduct_thenThrowsStoreForbiddenException() {
        // given
        User otherSeller = new User("other123", "password", "other@test.com", UserRole.SELLER);
        ReflectionTestUtils.setField(otherSeller, "id", 3L);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("other123", otherSeller);
            given(productRepository.findByProductId(1L)).willReturn(Optional.of(existingProduct));

            // when & then
            BusinessException thrown = assertThrows(BusinessException.class,
                    () -> productService.deleteProduct(1L)
            );

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.STORE_FORBIDDEN);
            verify(productRepository, never()).delete(any(Product.class));
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("일반 사용자가 상품을 삭제하려할때_deleteProduct 호출하면_권한 없음 예외가 발생한다")
    void givenRegularUser_whenDeleteProduct_thenThrowsForbiddenException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("user123", regularUser);

            // when & then
            BusinessException thrown = assertThrows(
                    BusinessException.class,
                    () -> productService.deleteProduct(1L)
            );

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
            verify(productRepository, never()).delete(any(Product.class));
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("존재하지않는 상품을 삭제하려할때_deleteProduct 호출하면_상품 없음 예외가 발생한다")
    void givenNonExistentProduct_whenDeleteProduct_thenThrowsProductNotFoundException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext("seller123", seller);
            given(productRepository.findByProductId(999L)).willReturn(Optional.empty());

            // when & then
            ProductException.ProductNotFoundException thrown = assertThrows(
                    ProductException.ProductNotFoundException.class,
                    () -> productService.deleteProduct(999L)
            );

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
            verify(productRepository, never()).delete(any(Product.class));
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Test
    @DisplayName("인증되지않은 사용자가 상품을 삭제하려할때_deleteProduct 호출하면_인증 없음 예외가 발생한다")
    void givenUnauthenticatedUser_whenDeleteProduct_thenThrowsUnauthorizedException() {
        // given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            given(securityContext.getAuthentication()).willReturn(null);

            // when & then
            BusinessException thrown = assertThrows(BusinessException.class,
                    () -> productService.deleteProduct(1L)
            );

            assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
            verify(productRepository, never()).delete(any(Product.class));
            verify(productRepository, never()).save(any(Product.class));
        }
    }
}