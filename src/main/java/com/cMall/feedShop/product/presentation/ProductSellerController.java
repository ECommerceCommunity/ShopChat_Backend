package com.cMall.feedShop.product.presentation;

import com.cMall.feedShop.common.aop.ApiResponseFormat;
import com.cMall.feedShop.product.application.dto.request.ProductCreateRequest;
import com.cMall.feedShop.product.application.dto.response.ProductCreateResponse;
import com.cMall.feedShop.product.application.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class ProductSellerController {
    private final ProductService productService;


    /**
     * 상품 등록 API
     * /api/seller/products
     * @param request
     * {
     *      String name;
     *      BigDecimal price;
     *      Long categoryId;
     *      DiscountType discountType;
     *      BigDecimal discountValue;
     *      String description;
     * }
     * @return 상품 등록 응답
     */
    @PostMapping("/products")
    @PreAuthorize("hasRole('SELLER')")
    @ApiResponseFormat(message = "상품 등록 완료")
    public ResponseEntity<ProductCreateResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }
}
