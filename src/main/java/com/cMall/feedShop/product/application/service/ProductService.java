package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;
import com.cMall.feedShop.product.application.dto.request.ProductCreateRequest;
import com.cMall.feedShop.product.application.dto.response.ProductCreateResponse;
import com.cMall.feedShop.product.application.dto.response.ProductListResponse;
import com.cMall.feedShop.product.application.dto.response.ProductPageResponse;
import com.cMall.feedShop.product.domain.model.Category;
import com.cMall.feedShop.product.domain.model.Product;
import com.cMall.feedShop.product.domain.repository.CategoryRepository;
import com.cMall.feedShop.product.domain.repository.ProductRepository;
import com.cMall.feedShop.product.application.util.DiscountCalculator;
import com.cMall.feedShop.store.domain.model.Store;
import com.cMall.feedShop.store.domain.repository.StoreRepository;
import com.cMall.feedShop.user.domain.enums.UserRole;
import com.cMall.feedShop.user.domain.model.User;
import com.cMall.feedShop.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DiscountCalculator discountCalculator;

    public ProductCreateResponse createProduct(ProductCreateRequest request)
    {
        // 1. 현재 사용자 ID 가져오기
        Long currentUserId = getCurrentUserId();

        // 2. 판매자 권한 검증
        validateSellerPermission(currentUserId);

        // 3. 사용자 스토어 찾기
        Store userStore = storeRepository.findBySellerId(currentUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // 4. 스토어 관리 권한 확인
        if (!userStore.isManagedBy(currentUserId)) {
            throw new BusinessException(ErrorCode.STORE_FORBIDDEN);
        }

        // 5. 카테고리 존재 확인
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // 6. 상품 생성
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .store(userStore)
                .category(category)
                .build();

        // 7. DB 저장
        Product savedProduct = productRepository.save(product);

        // 8. 응답값 리턴
        return ProductCreateResponse.of(savedProduct.getProductId());
    }

    // JWT 에서 현재 사용자 ID 추출 (추후 구현)
    private Long getCurrentUserId() {
        // JWT 토큰에서 사용자 ID 추출
        return 2L; // 임시. 현재 DB에 SELLER 권한 임시 유저의 id가 2임.
    }

    // 판매자 권한 검증
    private void validateSellerPermission(Long userId) {
        // 사용자가 ROLE_SELLER 권한을 가지고 있는지 확인
        // UserRepository 에서 사용자 조회 후 role 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != UserRole.SELLER) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    @Transactional(readOnly = true)
    public ProductPageResponse getProductList(int page, int size) {
        if (page < 0) {
            page = 0;
        }

        // 기본값 20, 최대 100
        if (size < 1 || size > 100) {
            size = 20;
        }

        Pageable pageable = PageRequest.of(page, size);

        // 삭제되지 않은 상품들을 Store와 함께 조회. (모든 상품을 페이지별로)
        Page<Product> productPage = productRepository.findByDeletedAtIsNullOrderByCreatedAtDesc(pageable);

        // 각각의 상품(Product 엔티티)을 ProductListResponse(응답값)로 변환한다.
        Page<ProductListResponse> responsePage = productPage.map(this::convertToProductListResponse);

        // ProductPageResponse 에서 상품 리스트 묶어서 페이지 정보 추가. 최종 응답값 리턴
        return ProductPageResponse.of(responsePage);
    }

    /**
     * 각각의 상품들을 ProductListResponse로 변환한다.
     */
    private ProductListResponse convertToProductListResponse(Product product) {

        // 할인가를 계산한다.
        BigDecimal discountPrice = discountCalculator.calculateDiscountPrice(
                product.getPrice(),
                product.getDiscountType(),
                product.getDiscountValue()
        );

        // ProductListResponse 에서 응답값(상품 정보)을 생성해준다.
        return ProductListResponse.of(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                discountPrice,
                product.getStore().getStoreId(),
                product.getStore().getStoreName(),
                product.getWishNumber()
        );
    }
}
