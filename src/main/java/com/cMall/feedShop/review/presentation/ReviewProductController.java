package com.cMall.feedShop.review.presentation;

import com.cMall.feedShop.review.application.dto.request.*;
import com.cMall.feedShop.review.application.dto.response.ProductReviewSummaryResponse;
import com.cMall.feedShop.review.application.dto.response.ReviewSummaryResponse;
import com.cMall.feedShop.review.application.ReviewService;
import com.cMall.feedShop.common.aop.ApiResponseFormat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Positive;

/**
 * SPRINT1 - 상품 중심 리뷰 API 컨트롤러
 * RE-02: 리뷰 목록 조회 (상품별)
 */
@Slf4j
@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
@Validated // 추가: 클래스 레벨에서 validation 활성화
@Tag(name = "Product Review", description = "상품 중심 리뷰 API - SPRINT1")
public class ReviewProductController {

    private final ReviewService reviewService;

    /**
     * RE-02: 리뷰 목록 조회 (상품별)
     * 특정 상품의 리뷰 목록을 페이징하여 조회합니다.
     */
    @ApiResponseFormat(message = "상품별 리뷰 목록이 성공적으로 조회되었습니다.")
    @GetMapping
    @Operation(summary = "상품별 리뷰 목록 조회", description = "특정 상품의 리뷰 목록을 페이징하여 조회합니다.")
    public ResponseEntity<ProductReviewSummaryResponse> getProductReviews(
            @PathVariable
            @Positive(message = "상품 ID는 양수여야 합니다") // 추가: validation 어노테이션
            Long productId,
            @PageableDefault(size = 10, page = 0) // 추가: 기본 페이징 설정
            Pageable pageable) {

        ProductReviewSummaryResponse reviews = reviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(reviews);
    }
}