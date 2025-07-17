package com.cMall.feedShop.review.presentation;
import com.cMall.feedShop.review.application.dto.request.*;
import com.cMall.feedShop.review.application.dto.response.ReviewDetailResponse;
import com.cMall.feedShop.review.application.ReviewService;
import com.cMall.feedShop.common.aop.ApiResponseFormat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Positive;

/**
 * SPRINT1 - 리뷰 범용 API 컨트롤러
 * RE-03: 리뷰 상세 조회
 */
@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Validated // 추가: 클래스 레벨에서 validation 활성화
@Tag(name = "Review", description = "리뷰 범용 API - SPRINT1")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * RE-03: 리뷰 상세 조회
     * 특정 리뷰의 상세 정보를 조회합니다.
     */
    @ApiResponseFormat(message = "리뷰가 성공적으로 조회되었습니다.")
    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 상세 조회", description = "특정 리뷰의 상세 정보를 조회합니다.")
    public ReviewDetailResponse getReviewDetail(
            @PathVariable
            @Positive(message = "리뷰 ID는 양수여야 합니다") // 추가: validation 어노테이션
            Long reviewId) {

        log.info("리뷰 상세 조회 요청 - reviewId: {}", reviewId);
        return reviewService.getReviewDetail(reviewId);
    }
}