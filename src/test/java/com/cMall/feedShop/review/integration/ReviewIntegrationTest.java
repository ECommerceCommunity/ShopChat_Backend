package com.cMall.feedShop.review.integration;

import com.cMall.feedShop.review.application.ReviewService;
import com.cMall.feedShop.review.application.ReviewStatisticsService;
import com.cMall.feedShop.review.application.dto.request.ReviewCreateRequest;
import com.cMall.feedShop.review.application.dto.response.ReviewCreateResponse;
import com.cMall.feedShop.review.application.dto.response.ReviewDetailResponse;
import com.cMall.feedShop.review.application.dto.response.ReviewStatisticsResponse;
import com.cMall.feedShop.review.domain.entity.*;
import com.cMall.feedShop.review.domain.repository.ReviewRepository;
import com.cMall.feedShop.review.domain.repository.ReviewImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@ActiveProfiles("test")
public class ReviewIntegrationTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewStatisticsService reviewStatisticsService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewImageRepository reviewImageRepository;

    private ReviewCreateRequest perfectShoeRequest;
    private ReviewCreateRequest badShoeRequest;

    @BeforeEach
    void setUp() {
        // 완벽한 신발 리뷰
        perfectShoeRequest = ReviewCreateRequest.builder()
                .userId(1L)
                .productId(1L)
                .reviewTitle("완벽한 신발입니다!")
                .rating(5)
                .content("사이즈 완벽, 쿠션 매우 부드럽고 안정감 최고")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();

        // 최악의 신발 리뷰
        badShoeRequest = ReviewCreateRequest.builder()
                .userId(2L)
                .productId(1L)
                .reviewTitle("최악의 신발")
                .rating(1)
                .content("사이즈 너무 작고 딱딱하며 불안정함")
                .sizeFit(SizeFit.VERY_SMALL)
                .cushioning(Cushion.VERY_FIRM)
                .stability(Stability.VERY_UNSTABLE)
                .imageUrls(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Integration: 완전한 리뷰 생성부터 조회까지의 전체 흐름")
    void completeReviewWorkflowIntegrationTest() {
        // Given: 리뷰 데이터 준비

        // When: 1단계 - 리뷰 생성
        ReviewCreateResponse createResponse = reviewService.createReview(perfectShoeRequest);

        // Then: 1단계 검증 - 리뷰가 정상 생성되었는지
        assertNotNull(createResponse);
        assertNotNull(createResponse.getReviewId());
        assertEquals(5, createResponse.getRating());
        assertEquals(SizeFit.PERFECT, createResponse.getSizeFit());
        assertEquals(Cushion.VERY_SOFT, createResponse.getCushioning());
        assertEquals(Stability.VERY_STABLE, createResponse.getStability());

        Long reviewId = createResponse.getReviewId();

        // When: 2단계 - 생성된 리뷰 상세 조회
        ReviewDetailResponse detailResponse = reviewService.getReviewDetail(reviewId);

        // Then: 2단계 검증 - 상세 조회 결과
        assertNotNull(detailResponse);
        assertEquals(reviewId, detailResponse.getReviewId());
        assertEquals("완벽한 신발입니다!", detailResponse.getReviewTitle());
        assertEquals(5, detailResponse.getRating());
        assertEquals(SizeFit.PERFECT, detailResponse.getSizeFit());
        assertEquals(Cushion.VERY_SOFT, detailResponse.getCushioning());
        assertEquals(Stability.VERY_STABLE, detailResponse.getStability());

        // When: 3단계 - 사용자별 리뷰 목록 조회
        Page<ReviewDetailResponse> userReviews = reviewService.getUserReviews(1L, PageRequest.of(0, 10));

        // Then: 3단계 검증 - 사용자 리뷰 목록
        assertNotNull(userReviews);
        assertEquals(1, userReviews.getTotalElements());
        assertEquals(reviewId, userReviews.getContent().get(0).getReviewId());
    }

    @Test
    @DisplayName("Integration: 다중 리뷰 생성 후 필터링 및 통계 생성")
    void multipleReviewsWithFilteringAndStatistics() {
        // Given: 여러 개의 다양한 리뷰 생성
        reviewService.createReview(perfectShoeRequest);
        reviewService.createReview(badShoeRequest);

        // 중간 수준 리뷰 추가
        ReviewCreateRequest mediumRequest = ReviewCreateRequest.builder()
                .userId(3L)
                .productId(1L)
                .reviewTitle("괜찮은 신발")
                .rating(3)
                .content("보통 수준의 신발입니다")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.NORMAL)
                .stability(Stability.NORMAL)
                .imageUrls(new ArrayList<>())
                .build();
        reviewService.createReview(mediumRequest);

        // When: 1단계 - 사이즈 핏별 필터링
        List<ReviewDetailResponse> perfectFitReviews =
                reviewService.getReviewsBySizeFit(1L, SizeFit.PERFECT);

        // Then: 1단계 검증 - PERFECT 핏 리뷰는 2개
        assertEquals(2, perfectFitReviews.size());

        // When: 2단계 - 쿠셔닝별 필터링
        List<ReviewDetailResponse> verySoftReviews =
                reviewService.getReviewsByCushioning(1L, Cushion.VERY_SOFT);

        // Then: 2단계 검증 - VERY_SOFT 쿠셔닝은 1개
        assertEquals(1, verySoftReviews.size());
        assertEquals(5, verySoftReviews.get(0).getRating());

        // When: 3단계 - 통계 생성
        ReviewStatisticsResponse statistics = reviewStatisticsService.getProductStatistics(1L);

        // Then: 3단계 검증 - 통계 계산
        assertNotNull(statistics);
        assertEquals(1L, statistics.getProductId());
        assertEquals(3L, statistics.getTotalReviews());
        assertEquals(3.0, statistics.getAverageRating(), 0.1); // (5+1+3)/3 = 3.0

        // 평점 분포 검증
        assertEquals(1L, statistics.getRatingDistribution().get(5)); // 5점 1개
        assertEquals(1L, statistics.getRatingDistribution().get(3)); // 3점 1개
        assertEquals(1L, statistics.getRatingDistribution().get(1)); // 1점 1개
    }

    @Test
    @DisplayName("Integration: 시간 경과에 따른 신발 특성 변화 업데이트")
    void shoeCharacteristicsChangeOverTime() {
        // Given: 초기에 완벽한 리뷰 작성
        ReviewCreateResponse initialReview = reviewService.createReview(perfectShoeRequest);
        Long reviewId = initialReview.getReviewId();

        // When: 시간이 지나서 특성이 변화 (신발이 늘어나고 쿠션이 주저앉음)
        com.cMall.feedShop.review.application.dto.request.ReviewUpdateRequest updateRequest =
                com.cMall.feedShop.review.application.dto.request.ReviewUpdateRequest.builder()
                        .reviewTitle("한 달 후 재평가")
                        .rating(3)
                        .content("처음엔 좋았지만 시간이 지나니 늘어나고 쿠션이 주저앉았어요")
                        .sizeFit(SizeFit.BIG)        // PERFECT → BIG (늘어남)
                        .cushioning(Cushion.FIRM)    // VERY_SOFT → FIRM (주저앉음)
                        .stability(Stability.UNSTABLE) // VERY_STABLE → UNSTABLE (안정성 저하)
                        .build();

        reviewService.updateReview(reviewId, updateRequest);

        // Then: 업데이트된 내용 검증
        ReviewDetailResponse updatedReview = reviewService.getReviewDetail(reviewId);

        assertEquals("한 달 후 재평가", updatedReview.getReviewTitle());
        assertEquals(3, updatedReview.getRating());
        assertEquals(SizeFit.BIG, updatedReview.getSizeFit());
        assertEquals(Cushion.FIRM, updatedReview.getCushioning());
        assertEquals(Stability.UNSTABLE, updatedReview.getStability());
        assertTrue(updatedReview.getContent().contains("주저앉았어요"));
    }

    @Test
    @DisplayName("Integration: 중복 리뷰 방지 및 예외 처리")
    void duplicateReviewPrevention() {
        // Given: 첫 번째 리뷰 작성
        reviewService.createReview(perfectShoeRequest);

        // When & Then: 같은 사용자가 같은 상품에 다시 리뷰 작성 시도
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reviewService.createReview(perfectShoeRequest)
        );

        assertEquals("이미 해당 상품에 대한 리뷰를 작성하셨습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("Integration: 5단계 특성별 평균 평점 계산")
    void averageRatingByCharacteristics() {
        // Given: 다양한 특성의 리뷰들 생성
        reviewService.createReview(perfectShoeRequest); // VERY_SOFT, 5점

        ReviewCreateRequest softRequest = ReviewCreateRequest.builder()
                .userId(4L)
                .productId(1L)
                .reviewTitle("부드러운 신발")
                .rating(4)
                .content("쿠션이 부드러워요")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.STABLE)
                .imageUrls(new ArrayList<>())
                .build();
        reviewService.createReview(softRequest); // VERY_SOFT, 4점

        // When: 특성별 평균 평점 조회
        Double verySoftAverage = reviewStatisticsService.getAverageRatingByCushioning(Cushion.VERY_SOFT);
        Double perfectFitAverage = reviewStatisticsService.getAverageRatingBySizeFit(SizeFit.PERFECT);
        Double veryStableAverage = reviewStatisticsService.getAverageRatingByStability(Stability.VERY_STABLE);

        // Then: 계산된 평균 검증
        assertEquals(4.5, verySoftAverage, 0.1); // (5+4)/2 = 4.5
        assertEquals(4.5, perfectFitAverage, 0.1); // (5+4)/2 = 4.5
        assertEquals(5.0, veryStableAverage, 0.1); // 5/1 = 5.0
    }
}