package com.cMall.feedShop.review.presentation;

import com.cMall.feedShop.common.dto.ApiResponse;
import com.cMall.feedShop.review.application.ReviewService;
import com.cMall.feedShop.review.application.dto.request.ReviewRequest;
import com.cMall.feedShop.review.application.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "리뷰 관리", description = "리뷰 등록, 수정, 삭제, 조회 API")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @PostMapping
    @Operation(summary = "리뷰 등록", description = "새로운 리뷰를 등록합니다.")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request) {
        
        log.info("리뷰 등록 요청 - 사용자: {}, 상품: {}", request.getUserId(), request.getProductId());
        
        ReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("리뷰가 성공적으로 등록되었습니다.", response));
    }
    
    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "기존 리뷰를 수정합니다.")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable @Min(1) Long reviewId,
            @Valid @RequestBody ReviewRequest request) {
        
        log.info("리뷰 수정 요청 - ID: {}", reviewId);
        
        ReviewResponse response = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(ApiResponse.success("리뷰가 성공적으로 수정되었습니다.", response));
    }
    
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable @Min(1) Long reviewId,
            @RequestParam @Min(1) Long userId) {
        
        log.info("리뷰 삭제 요청 - ID: {}, 사용자: {}", reviewId, userId);
        
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponse.success("리뷰가 성공적으로 삭제되었습니다.", null));
    }
    
    @PatchMapping("/{reviewId}/deactivate")
    @Operation(summary = "리뷰 비활성화", description = "리뷰를 비활성화합니다.")
    public ResponseEntity<ApiResponse<Void>> deactivateReview(
            @PathVariable @Min(1) Long reviewId,
            @RequestParam @Min(1) Long userId) {
        
        log.info("리뷰 비활성화 요청 - ID: {}, 사용자: {}", reviewId, userId);
        
        reviewService.deactivateReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponse.success("리뷰가 성공적으로 비활성화되었습니다.", null));
    }
    
    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 단건 조회", description = "특정 리뷰의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(
            @PathVariable @Min(1) Long reviewId) {
        
        ReviewResponse response = reviewService.getReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/product/{productId}")
    @Operation(summary = "상품별 리뷰 목록 조회", description = "특정 상품의 리뷰 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviewsByProductId(
            @PathVariable @Min(1) Long productId,
            @RequestParam(defaultValue = "false") boolean summary,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<ReviewResponse> response = summary 
            ? reviewService.getReviewsSummaryByProductId(productId, pageable)
            : reviewService.getReviewsByProductId(productId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 리뷰 목록 조회", description = "특정 사용자의 리뷰 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviewsByUserId(
            @PathVariable @Min(1) Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<ReviewResponse> response = reviewService.getReviewsByUserId(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/search")
    @Operation(summary = "리뷰 검색", description = "조건에 따라 리뷰를 검색합니다.")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> searchReviews(
            @RequestParam @Min(1) Long productId,
            @RequestParam(required = false) @Parameter(description = "최소 평점 (1-5)") Integer minRating,
            @RequestParam(required = false) @Parameter(description = "최대 평점 (1-5)") Integer maxRating,
            @RequestParam(required = false) @Parameter(description = "검색 키워드") String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<ReviewResponse> response = reviewService.getReviewsWithFilters(
                productId, minRating, maxRating, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/product/{productId}/statistics")
    @Operation(summary = "상품 리뷰 통계", description = "특정 상품의 평균 평점과 리뷰 개수를 조회합니다.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReviewStatistics(
            @PathVariable @Min(1) Long productId) {
        
        Double averageRating = reviewService.getAverageRating(productId);
        Long reviewCount = reviewService.getReviewCount(productId);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("averageRating", averageRating);
        statistics.put("reviewCount", reviewCount);
        
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @GetMapping("/product/{productId}/rating")
    @Operation(summary = "상품 평균 평점 조회", description = "특정 상품의 평균 평점을 조회합니다.")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(
            @PathVariable @Min(1) Long productId) {
        
        Double averageRating = reviewService.getAverageRating(productId);
        return ResponseEntity.ok(ApiResponse.success(averageRating));
    }
}