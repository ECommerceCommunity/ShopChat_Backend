package com.cMall.feedShop.review.presentation;

import com.cMall.feedShop.common.dto.ApiResponse;
import com.cMall.feedShop.review.application.dto.request.ReviewRequest;
import com.cMall.feedShop.review.application.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.Map;

@Tag(name = "리뷰 관리 API", description = "리뷰 등록, 수정, 삭제, 조회 관련 API")
@Validated
public interface ReviewApi {
    
    @Operation(summary = "리뷰 등록", description = "새로운 리뷰를 등록합니다.")
    ResponseEntity<ApiResponse<ReviewResponse>> createReview(
        @Valid @RequestBody ReviewRequest request
    );
    
    @Operation(summary = "리뷰 수정", description = "기존 리뷰의 제목, 내용, 평점을 수정합니다.")
    ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
        @PathVariable @Min(1) @Parameter(description = "리뷰 ID") Long reviewId,
        @Valid @RequestBody ReviewRequest request
    );
    
    @Operation(summary = "리뷰 삭제", description = "리뷰를 논리 삭제합니다.")
    ResponseEntity<ApiResponse<Void>> deleteReview(
        @PathVariable @Min(1) @Parameter(description = "리뷰 ID") Long reviewId,
        @RequestParam @Min(1) @Parameter(description = "사용자 ID") Long userId
    );
    
    @Operation(summary = "리뷰 비활성화", description = "리뷰를 비활성화합니다.")
    ResponseEntity<ApiResponse<Void>> deactivateReview(
        @PathVariable @Min(1) @Parameter(description = "리뷰 ID") Long reviewId,
        @RequestParam @Min(1) @Parameter(description = "사용자 ID") Long userId
    );
    
    @Operation(summary = "리뷰 단건 조회", description = "특정 리뷰의 상세 정보를 조회합니다.")
    ResponseEntity<ApiResponse<ReviewResponse>> getReview(
        @PathVariable @Min(1) @Parameter(description = "리뷰 ID") Long reviewId
    );
    
    @Operation(summary = "상품별 리뷰 목록 조회", description = "특정 상품의 리뷰 목록을 페이징하여 조회합니다.")
    ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviewsByProductId(
        @PathVariable @Min(1) @Parameter(description = "상품 ID") Long productId,
        @RequestParam(defaultValue = "false") @Parameter(description = "요약 모드 여부") boolean summary,
        @Parameter(description = "페이징 정보") Pageable pageable
    );
    
    @Operation(summary = "사용자별 리뷰 목록 조회", description = "특정 사용자가 작성한 리뷰 목록을 페이징하여 조회합니다.")
    ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviewsByUserId(
        @PathVariable @Min(1) @Parameter(description = "사용자 ID") Long userId,
        @Parameter(description = "페이징 정보") Pageable pageable
    );
    
    @Operation(summary = "리뷰 검색", description = "평점 범위와 키워드를 이용하여 리뷰를 검색합니다.")
    ResponseEntity<ApiResponse<Page<ReviewResponse>>> searchReviews(
        @RequestParam @Min(1) @Parameter(description = "상품 ID") Long productId,
        @RequestParam(required = false) @Parameter(description = "최소 평점") Integer minRating,
        @RequestParam(required = false) @Parameter(description = "최대 평점") Integer maxRating,
        @RequestParam(required = false) @Parameter(description = "검색 키워드") String keyword,
        @Parameter(description = "페이징 정보") Pageable pageable
    );
    
    @Operation(summary = "상품 리뷰 통계", description = "특정 상품의 평균 평점과 리뷰 개수를 조회합니다.")
    ResponseEntity<ApiResponse<Map<String, Object>>> getReviewStatistics(
        @PathVariable @Min(1) @Parameter(description = "상품 ID") Long productId
    );
    
    @Operation(summary = "상품 평균 평점 조회", description = "특정 상품의 평균 평점을 조회합니다.")
    ResponseEntity<ApiResponse<Double>> getAverageRating(
        @PathVariable @Min(1) @Parameter(description = "상품 ID") Long productId
    );
}