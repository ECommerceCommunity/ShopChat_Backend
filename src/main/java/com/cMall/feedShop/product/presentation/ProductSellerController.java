package com.cMall.feedShop.product.presentation;

import com.cMall.feedShop.common.aop.ApiResponseFormat;
import com.cMall.feedShop.common.dto.ApiResponse;
import com.cMall.feedShop.product.application.dto.request.ProductCreateRequest;
import com.cMall.feedShop.product.application.dto.response.ProductCreateResponse;
import com.cMall.feedShop.product.application.service.ProductCreateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class ProductSellerController {
    private final ProductCreateService productCreateService;

    /**
     * 상품 등록 API (이미지와 옵션 포함)
     * POST /api/seller/products
     *
     * @param request
     * {
     *   "name": "상품명",
     *   "price": 50000,
     *   "categoryId": 1,
     *   "discountType": "RATE_DISCOUNT",
     *   "discountValue": 10,
     *   "description": "상품 설명",
     *   "images": [
     *     {
     *       "url": "https://example.com/main.jpg",
     *       "type": "MAIN"
     *     },
     *     {
     *       "url": "https://example.com/detail1.jpg",
     *       "type": "DETAIL"
     *     }
     *   ],
     *   "options": [
     *     {
     *       "gender": "UNISEX",
     *       "size": "SIZE_250",
     *       "color": "BLACK",
     *       "stock": 100
     *     },
     *     {
     *       "gender": "UNISEX",
     *       "size": "SIZE_255",
     *       "color": "BLACK",
     *       "stock": 50
     *     }
     *   ]
     * }
     *
     * @return 상품 등록 응답
     */
    @PostMapping("/products")
    @PreAuthorize("hasRole('SELLER')")
    @ApiResponseFormat(message = "상품이 성공적으로 등록되었습니다.")
    public ApiResponse<ProductCreateResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductCreateResponse data = productCreateService.createProduct(request);
        return ApiResponse.success(data);
    }
}
