package com.cMall.feedShop.review.application.dto.response;

import com.cMall.feedShop.review.domain.Review;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    
    private Long reviewId;
    private Long userId;
    private Long productId;
    private String reviewTitle;
    private Integer rating;
    private String content;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Domain Entity에서 DTO로 변환
    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUserId())
                .productId(review.getProductId())
                .reviewTitle(review.getReviewTitle())
                .rating(review.getRating())
                .content(review.getContent())
                .status(review.getStatus().name())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
    
    // Entity -> DTO 변환 (요약 버전 - 목록 조회용)
    public static ReviewResponse summaryFrom(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUserId())
                .productId(review.getProductId())
                .reviewTitle(review.getReviewTitle())
                .rating(review.getRating())
                .content(truncateContent(review.getContent(), 100)) // 내용 100자 제한
                .status(review.getStatus().name())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
    
    // 공개용 변환 (민감한 정보 제외)
    public static ReviewResponse publicFrom(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                // userId는 제외 (개인정보 보호)
                .productId(review.getProductId())
                .reviewTitle(review.getReviewTitle())
                .rating(review.getRating())
                .content(review.getContent())
                .status(review.getStatus().name())
                .createdAt(review.getCreatedAt())
                .build();
    }
    
    // 유틸리티 메서드들
    private static String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
    
    // 비즈니스 메서드들
    public boolean isRecentReview() {
        return this.createdAt != null && 
               this.createdAt.isAfter(LocalDateTime.now().minusDays(7));
    }
    
    public boolean isHighRating() {
        return this.rating != null && this.rating >= 4;
    }
    
    public String getContentPreview() {
        return truncateContent(this.content, 50);
    }
    
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }
}