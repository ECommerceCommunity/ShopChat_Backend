package com.cMall.feedShop.review.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewStatisticsResponse {

    private Long productId;

    private Double averageRating;

    private Long totalReviews;

    private Map<Integer, Long> ratingDistribution;

    private Map<String, Long> sizeFitDistribution;

    private Map<String, Long> stabilityDistribution;

    private Map<String, Long> cushioningDistribution;
}