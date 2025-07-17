package com.cMall.feedShop.review.presentation;

import com.cMall.feedShop.review.application.dto.request.ReviewCreateRequest;
import com.cMall.feedShop.review.application.dto.response.ReviewCreateResponse;
import com.cMall.feedShop.review.application.ReviewService;
import com.cMall.feedShop.common.aop.ApiResponseFormat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * SPRINT1 - 사용자 중심 리뷰 API 컨트롤러
 * RE-01: 리뷰 작성
 */
@Slf4j
@RestController
@RequestMapping("/api/users/{userId}/reviews")
@RequiredArgsConstructor
@Validated // 추가: 클래스 레벨에서 validation 활성화
@Tag(name = "User Review", description = "사용자 중심 리뷰 API - SPRINT1")
public class ReviewUserController {

    private final ReviewService reviewService;

    /**
     * RE-01: 리뷰 작성
     * 새로운 리뷰를 작성합니다.
     */
    @ApiResponseFormat(message = "리뷰가 성공적으로 등록되었습니다.")
    @PostMapping
    @PreAuthorize("#userId == authentication.principal.userId or hasRole('ADMIN')")
    @Operation(summary = "리뷰 등록", description = "새로운 리뷰를 등록합니다.")
    public ReviewCreateResponse createReview(
            @PathVariable
            @Positive(message = "사용자 ID는 양수여야 합니다") // 추가: validation 어노테이션
            Long userId,
            @Valid @RequestBody ReviewCreateRequest request) {

        log.info("리뷰 등록 요청 - 사용자: {}, 상품: {}", userId, request.getProductId());
        return reviewService.createReview(request);
    }
}