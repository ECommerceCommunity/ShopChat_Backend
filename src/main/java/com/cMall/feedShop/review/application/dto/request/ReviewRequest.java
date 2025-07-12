package com.cMall.feedShop.review.application.dto.request;

import com.cMall.feedShop.review.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {
    
    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;
    
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
    
    @NotBlank(message = "리뷰 제목은 필수입니다.")
    @Size(max = 100, message = "리뷰 제목은 100자 이하여야 합니다.")
    private String reviewTitle;
    
    @NotNull(message = "평점은 필수입니다.")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
    private Integer rating;
    
    @Size(max = 1000, message = "리뷰 내용은 1000자 이하여야 합니다.")
    private String content;
    
    // Domain Entity로 변환 (도메인 검증 포함)
    public Review toEntity() {
        // 도메인 검증 로직 호출
        Review.validateRating(this.rating);
        Review.validateContent(this.content);
        Review.validateTitle(this.reviewTitle);
        
        return Review.builder()
                .userId(this.userId)
                .productId(this.productId)
                .reviewTitle(this.reviewTitle)
                .rating(this.rating)
                .content(this.content)
                .build();
    }
}