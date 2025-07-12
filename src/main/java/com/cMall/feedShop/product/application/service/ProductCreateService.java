package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;
import com.cMall.feedShop.product.application.dto.request.ProductCreateRequest;
import com.cMall.feedShop.product.application.dto.request.ProductImageRequest;
import com.cMall.feedShop.product.application.dto.request.ProductOptionRequest;
import com.cMall.feedShop.product.application.dto.response.ProductCreateResponse;
import com.cMall.feedShop.product.application.exception.ProductException;
import com.cMall.feedShop.product.domain.model.Category;
import com.cMall.feedShop.product.domain.model.Product;
import com.cMall.feedShop.product.domain.model.ProductImage;
import com.cMall.feedShop.product.domain.model.ProductOption;
import com.cMall.feedShop.product.domain.repository.CategoryRepository;
import com.cMall.feedShop.product.domain.repository.ProductRepository;
import com.cMall.feedShop.store.application.exception.StoreException;
import com.cMall.feedShop.store.domain.model.Store;
import com.cMall.feedShop.store.domain.repository.StoreRepository;
import com.cMall.feedShop.user.domain.enums.UserRole;
import com.cMall.feedShop.user.domain.model.User;
import com.cMall.feedShop.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCreateService {
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 상품 등록
    public ProductCreateResponse createProduct(ProductCreateRequest request) {
        // 1. 현재 사용자 ID 가져오기
        Long currentUserId = getCurrentUserId();

        // 2. 판매자 권한 검증
        validateSellerPermission(currentUserId);

        // 3. 사용자 스토어 찾기
        Store userStore = storeRepository.findBySellerId(currentUserId)
                .orElseThrow(() -> new StoreException.StoreNotFoundException());

        // 4. 스토어 관리 권한 확인
        if (!userStore.isManagedBy(currentUserId)) {
            throw new StoreException.StoreForbiddenException();
        }

        // 5. 카테고리 존재 확인
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ProductException.CategoryNotFoundException());

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

        // 7. 상품 이미지 생성 및 저장 (메모리상에서만)
        createProductImages(product, request.getImages());

        // 8. 상품에 옵션 추가 (메모리상에서만)
        createProductOptions(product, request.getOptions());

        // 9. DB 저장
        Product savedProduct = productRepository.save(product);

        // 10. 응답값 리턴
        return ProductCreateResponse.of(savedProduct.getProductId());
    }

    // 상품 이미지 생성
    private void createProductImages(Product product, List<ProductImageRequest> requests)
    {
        List<ProductImage> productImages = requests.stream()
                .map(request -> new ProductImage(
                        request.getUrl(),
                        request.getType(),
                        product
                ))
                .toList();

        // Product 엔티티에 이미지 추가
        product.getProductImages().addAll(productImages);
    }

    // 상품 옵션 생성
    public void createProductOptions(Product product, List<ProductOptionRequest> requests)
    {
        List<ProductOption> productOptions = requests.stream()
                .map(request -> new ProductOption(
                        request.getGender(),
                        request.getSize(),
                        request.getColor(),
                        request.getStock(),
                        product
                ))
                .toList();

        // Product 엔티티에 옵션 추가
        product.getProductOptions().addAll(productOptions);
    }

    // JWT 에서 현재 사용자 ID 추출 (추후 구현)
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String login_id = authentication.getName();

        User user = userRepository.findByLoginId(login_id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return user.getId();
    }

    // 판매자 권한 검증
    private void validateSellerPermission(Long userId) {
        // 사용자가 SELLER 권한을 가지고 있는지 확인
        // UserRepository 에서 사용자 조회 후 role 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != UserRole.SELLER) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
