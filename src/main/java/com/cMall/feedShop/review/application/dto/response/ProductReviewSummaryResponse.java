// 상품 전체 리뷰 통계

package com.cMall.feedShop.review.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewSummaryResponse {
    
    private Long productId;
    private Long totalReviews;
    private Double averageRating;
    private RatingDistribution ratingDistribution;
    private String mostCommonSizeFit; // 가장 많이 선택된 사이즈 핏
    private List<ReviewSummaryResponse> recentReviews; // 기존 ReviewSummaryResponse 사용
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RatingDistribution {
        private Long fiveStar;
        private Long fourStar;
        private Long threeStar;
        private Long twoStar;
        private Long oneStar;
    }
}