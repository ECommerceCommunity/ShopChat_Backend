package com.cMall.feedShop.review.application;

import com.cMall.feedShop.review.domain.repository.ReviewRepository;
import com.cMall.feedShop.review.domain.entity.ReviewStatus;
import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Stability;
import com.cMall.feedShop.review.application.dto.response.ReviewStatisticsResponse;
import com.cMall.feedShop.review.application.dto.response.ProductReviewSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewStatisticsService {

    private final ReviewRepository reviewRepository;

    /**
     * 상품별 리뷰 통계 조회
     */
    public ReviewStatisticsResponse getProductStatistics(Long productId) {
        // 평균 평점 조회
        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);
        if (averageRating == null) {
            throw new IllegalArgumentException("해당 상품의 리뷰가 없습니다.");
        }

        // 총 리뷰 수 조회
        Long totalReviews = reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.ACTIVE);

        // 평점 분포 조회
        Map<Integer, Long> ratingDistribution = Map.of(
                5, reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 5),
                4, reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 4),
                3, reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 3),
                2, reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 2),
                1, reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 1)
        );

        // 사이즈 핏 분포 (예시)
        Map<String, Long> sizeFitDistribution = Map.of(
                "PERFECT", 30L,
                "BIG", 15L,
                "SMALL", 5L
        );

        // 안정성 분포 (예시)
        Map<String, Long> stabilityDistribution = Map.of(
                "VERY_STABLE", 25L,
                "STABLE", 20L,
                "NORMAL", 5L
        );

        return ReviewStatisticsResponse.builder()
                .productId(productId)
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .ratingDistribution(ratingDistribution)
                .sizeFitDistribution(sizeFitDistribution)
                .stabilityDistribution(stabilityDistribution)
                .build();
    }

    /**
     * 상품별 리뷰 요약 정보 조회
     */
    public ProductReviewSummaryResponse getProductReviewSummary(Long productId) {
        // 평균 평점 조회
        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);

        // 총 리뷰 수 조회
        Long totalReviews = reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.ACTIVE);

        // 평점 분포 생성
        ProductReviewSummaryResponse.RatingDistribution ratingDistribution =
                ProductReviewSummaryResponse.RatingDistribution.builder()
                        .fiveStar(reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 5))
                        .fourStar(reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 4))
                        .threeStar(reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 3))
                        .twoStar(reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 2))
                        .oneStar(reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 1))
                        .build();

        return ProductReviewSummaryResponse.builder()
                .productId(productId)
                .totalReviews(totalReviews)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .ratingDistribution(ratingDistribution)
                .mostCommonSizeFit("PERFECT") // 임시값
                .recentReviews(new ArrayList<>()) // 임시값
                .build();
    }

    /**
     * 쿠셔닝별 평균 평점 조회
     */
    public Double getAverageRatingByCushioning(Cushion cushioning) {
        return reviewRepository.findAverageRatingByCushioning(cushioning, ReviewStatus.ACTIVE);
    }

    /**
     * 사이즈 핏별 평균 평점 조회
     */
    public Double getAverageRatingBySizeFit(SizeFit sizeFit) {
        return reviewRepository.findAverageRatingBySizeFit(sizeFit, ReviewStatus.ACTIVE);
    }

    /**
     * 안정성별 평균 평점 조회
     */
    public Double getAverageRatingByStability(Stability stability) {
        return reviewRepository.findAverageRatingByStability(stability, ReviewStatus.ACTIVE);
    }
}