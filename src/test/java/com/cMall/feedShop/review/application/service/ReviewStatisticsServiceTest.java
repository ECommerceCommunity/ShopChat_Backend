package com.cMall.feedShop.review.application.service;

import com.cMall.feedShop.review.application.ReviewStatisticsService;
import com.cMall.feedShop.review.domain.repository.ReviewRepository;
import com.cMall.feedShop.review.domain.entity.ReviewStatus;
import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Stability;
import com.cMall.feedShop.review.application.dto.response.ReviewStatisticsResponse;
import com.cMall.feedShop.review.application.dto.response.ProductReviewSummaryResponse;
import com.cMall.feedShop.review.application.dto.response.ReviewSummaryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
class ReviewStatisticsServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewStatisticsService reviewStatisticsService;

    @Test
    @DisplayName("Given product id_When get statistics_Then return statistics response")
    void givenProductId_whenGetStatistics_thenReturnStatisticsResponse() {
        // given
        Long productId = 1L;

        // Repository 메서드 mocking
        when(reviewRepository.findAverageRatingByProductId(productId)).thenReturn(4.5);
        when(reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.ACTIVE)).thenReturn(10L);

        // 평점별 분포 mocking
        when(reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 5)).thenReturn(6L);
        when(reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 4)).thenReturn(3L);
        when(reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 3)).thenReturn(1L);
        when(reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 2)).thenReturn(0L);
        when(reviewRepository.countByProductIdAndStatusAndRating(productId, ReviewStatus.ACTIVE, 1)).thenReturn(0L);

        // when
        ReviewStatisticsResponse response = reviewStatisticsService.getProductStatistics(productId);

        // then
        assertNotNull(response);
        assertEquals(productId, response.getProductId());
        assertEquals(4.5, response.getAverageRating());
        assertEquals(10L, response.getTotalReviews());
        assertEquals(6L, response.getRatingDistribution().get(5));
        verify(reviewRepository, times(1)).findAverageRatingByProductId(productId);
        verify(reviewRepository, times(1)).countByProductIdAndStatus(productId, ReviewStatus.ACTIVE);
    }

    @Test
    @DisplayName("Given product id_When get product summary_Then return summary response")
    void givenProductId_whenGetProductSummary_thenReturnSummaryResponse() {
        // given
        Long productId = 1L;

        // 최근 리뷰 목록 생성
        List<ReviewSummaryResponse> recentReviews = List.of(
                ReviewSummaryResponse.builder()
                        .reviewId(1L)
                        .userId(1L)
                        .productId(productId)
                        .reviewTitle("최고예요!")
                        .content("정말 좋은 상품입니다")
                        .rating(5)
                        .sizeFit(SizeFit.PERFECT)
                        .cushioning(Cushion.VERY_SOFT)
                        .stability(Stability.VERY_STABLE)
                        .createdAt(LocalDateTime.now())
                        .images(new ArrayList<>())
                        .build(),
                ReviewSummaryResponse.builder()
                        .reviewId(2L)
                        .userId(2L)
                        .productId(productId)
                        .reviewTitle("편해요")
                        .content("편하고 좋습니다")
                        .rating(4)
                        .sizeFit(SizeFit.PERFECT)
                        .cushioning(Cushion.SOFT)
                        .stability(Stability.STABLE)
                        .createdAt(LocalDateTime.now())
                        .images(new ArrayList<>())
                        .build()
        );

        // Repository mocking
        when(reviewRepository.findAverageRatingByProductId(productId)).thenReturn(4.2);
        when(reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.ACTIVE)).thenReturn(25L);

        // when
        ProductReviewSummaryResponse response = reviewStatisticsService.getProductReviewSummary(productId);

        // then
        assertNotNull(response);
        assertEquals(productId, response.getProductId());
        assertEquals(4.2, response.getAverageRating());
        assertEquals(25L, response.getTotalReviews());
        verify(reviewRepository, times(1)).findAverageRatingByProductId(productId);
        verify(reviewRepository, times(1)).countByProductIdAndStatus(productId, ReviewStatus.ACTIVE);
    }

    @Test
    @DisplayName("Given cushioning type_When get average rating_Then return calculated average")
    void givenCushioningType_whenGetAverageRating_thenReturnCalculatedAverage() {
        // given
        Cushion cushioningType = Cushion.VERY_SOFT;
        Double expectedRating = 4.7;

        when(reviewRepository.findAverageRatingByCushioning(cushioningType, ReviewStatus.ACTIVE))
                .thenReturn(expectedRating);

        // when
        Double actualRating = reviewStatisticsService.getAverageRatingByCushioning(cushioningType);

        // then
        assertEquals(expectedRating, actualRating);
        verify(reviewRepository, times(1))
                .findAverageRatingByCushioning(cushioningType, ReviewStatus.ACTIVE);
    }

    @Test
    @DisplayName("Given size fit type_When get average rating_Then return calculated average")
    void givenSizeFitType_whenGetAverageRating_thenReturnCalculatedAverage() {
        // given
        SizeFit sizeFitType = SizeFit.PERFECT;
        Double expectedRating = 4.5;

        when(reviewRepository.findAverageRatingBySizeFit(sizeFitType, ReviewStatus.ACTIVE))
                .thenReturn(expectedRating);

        // when
        Double actualRating = reviewStatisticsService.getAverageRatingBySizeFit(sizeFitType);

        // then
        assertEquals(expectedRating, actualRating);
        verify(reviewRepository, times(1))
                .findAverageRatingBySizeFit(sizeFitType, ReviewStatus.ACTIVE);
    }

    @Test
    @DisplayName("Given stability type_When get average rating_Then return calculated average")
    void givenStabilityType_whenGetAverageRating_thenReturnCalculatedAverage() {
        // given
        Stability stabilityType = Stability.VERY_STABLE;
        Double expectedRating = 4.8;

        when(reviewRepository.findAverageRatingByStability(stabilityType, ReviewStatus.ACTIVE))
                .thenReturn(expectedRating);

        // when
        Double actualRating = reviewStatisticsService.getAverageRatingByStability(stabilityType);

        // then
        assertEquals(expectedRating, actualRating);
        verify(reviewRepository, times(1))
                .findAverageRatingByStability(stabilityType, ReviewStatus.ACTIVE);
    }

    @Test
    @DisplayName("Given invalid product id_When get statistics_Then throw exception")
    void givenInvalidProductId_whenGetStatistics_thenThrowException() {
        // given
        Long invalidProductId = 999L;

        when(reviewRepository.findAverageRatingByProductId(invalidProductId)).thenReturn(null);
        when(reviewRepository.countByProductIdAndStatus(invalidProductId, ReviewStatus.ACTIVE)).thenReturn(0L);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            reviewStatisticsService.getProductStatistics(invalidProductId);
        });
    }
}