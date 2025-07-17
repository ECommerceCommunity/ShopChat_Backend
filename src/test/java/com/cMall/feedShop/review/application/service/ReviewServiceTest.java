package com.cMall.feedShop.review.application.service;

import com.cMall.feedShop.review.application.ReviewService;
import com.cMall.feedShop.review.domain.entity.Review;
import com.cMall.feedShop.review.domain.entity.ReviewStatus;
import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.Stability;
import com.cMall.feedShop.review.domain.repository.ReviewRepository;
import com.cMall.feedShop.review.domain.repository.ReviewImageRepository; // 추가
import com.cMall.feedShop.review.application.dto.request.ReviewCreateRequest;
import com.cMall.feedShop.review.application.dto.request.ReviewUpdateRequest;
import com.cMall.feedShop.review.application.dto.response.ReviewCreateResponse;
import com.cMall.feedShop.review.application.dto.response.ReviewDetailResponse;
import com.cMall.feedShop.review.application.exception.ReviewException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewImageRepository reviewImageRepository; // Mock 추가

    @InjectMocks
    private ReviewService reviewService;

    private ReviewCreateRequest createRequest;
    private Review review;

    @BeforeEach
    void setUp() {
        createRequest = ReviewCreateRequest.builder()
                .content("정말 편한 신발입니다. 하루 종일 신고 다녀도 발이 전혀 아프지 않아요")
                .rating(5)
                .userId(1L)
                .productId(1L)
                .sizeFit(SizeFit.PERFECT)      // 딱 맞음
                .cushioning(Cushion.VERY_SOFT) // 매우 부드러움
                .stability(Stability.VERY_STABLE) // 매우 안정적
                .build();

        review = Review.builder()
                .reviewId(1L)
                .content("정말 편한 신발입니다. 하루 종일 신고 다녀도 발이 전혀 아프지 않아요")
                .rating(5)
                .userId(1L)
                .productId(1L)
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .status(ReviewStatus.ACTIVE)
                .build();
    }

    // RE-01: 신발 리뷰 작성 기능 테스트 (5단계 평가)
    @Test
    @DisplayName("Given 5-level shoe characteristics_When create review_Then return detailed response")
    void given5LevelShoeCharacteristics_whenCreateReview_thenReturnDetailedResponse() {
        // given
        when(reviewRepository.existsByUserIdAndProductIdAndStatusActive(1L, 1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // when
        ReviewCreateResponse response = reviewService.createReview(createRequest);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getReviewId());
        assertEquals(SizeFit.PERFECT, response.getSizeFit());
        assertEquals(Cushion.VERY_SOFT, response.getCushioning());
        assertEquals(Stability.VERY_STABLE, response.getStability());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("Given extreme negative characteristics_When create review_Then handle all extreme values")
    void givenExtremeNegativeCharacteristics_whenCreateReview_thenHandleAllExtremeValues() {
        // given - 최악의 신발 리뷰
        ReviewCreateRequest extremeRequest = ReviewCreateRequest.builder()
                .content("정말 최악의 신발입니다. 사이즈도 안 맞고 쿠션도 없고 발목도 불안정해요")
                .rating(1)
                .userId(1L)
                .productId(2L)
                .sizeFit(SizeFit.VERY_SMALL)      // 매우 작음
                .cushioning(Cushion.VERY_FIRM)    // 매우 단단함
                .stability(Stability.VERY_UNSTABLE) // 매우 불안정
                .build();

        Review extremeReview = Review.builder()
                .reviewId(2L)
                .content(extremeRequest.getContent())
                .rating(1)
                .userId(1L)
                .productId(2L)
                .sizeFit(SizeFit.VERY_SMALL)
                .cushioning(Cushion.VERY_FIRM)
                .stability(Stability.VERY_UNSTABLE)
                .status(ReviewStatus.ACTIVE)
                .build();

        when(reviewRepository.existsByUserIdAndProductIdAndStatusActive(1L, 2L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(extremeReview);

        // when
        ReviewCreateResponse response = reviewService.createReview(extremeRequest);

        // then
        assertNotNull(response);
        assertEquals(1, response.getRating());
        assertEquals(SizeFit.VERY_SMALL, response.getSizeFit());
        assertEquals(Cushion.VERY_FIRM, response.getCushioning());
        assertEquals(Stability.VERY_UNSTABLE, response.getStability());
        assertTrue(response.getContent().contains("최악의 신발"));
    }

    // RE-02: 5단계 필터링으로 리뷰 목록 조회
    @Test
    @DisplayName("Given very big size filter_When get filtered reviews_Then return matching reviews")
    void givenVeryBigSizeFilter_whenGetFilteredReviews_thenReturnMatchingReviews() {
        // given
        Long productId = 1L;
        SizeFit targetSizeFit = SizeFit.VERY_BIG; // 매우 큰 사이즈만 필터링

        Review bigSizeReview = Review.builder()
                .reviewId(1L)
                .content("사이즈가 매우 커서 발이 헐렁거려요")
                .sizeFit(SizeFit.VERY_BIG)
                .cushioning(Cushion.NORMAL)
                .stability(Stability.NORMAL)
                .status(ReviewStatus.ACTIVE)
                .build();

        List<Review> reviews = List.of(bigSizeReview);
        when(reviewRepository.findByProductIdAndSizeFitAndStatus(productId, targetSizeFit, ReviewStatus.ACTIVE))
                .thenReturn(reviews);

        // ReviewImageRepository Mock 설정 추가
        when(reviewImageRepository.findByReviewIdOrderByImageOrder(any())).thenReturn(new ArrayList<>());

        // when
        List<ReviewDetailResponse> responses = reviewService.getReviewsBySizeFit(productId, targetSizeFit);

        // then
        assertEquals(1, responses.size());
        assertEquals(SizeFit.VERY_BIG, responses.get(0).getSizeFit());
        assertTrue(responses.get(0).getContent().contains("매우 커서"));
    }

    @Test
    @DisplayName("Given very soft cushioning filter_When get reviews_Then return ultra comfort reviews")
    void givenVerySoftCushioningFilter_whenGetReviews_thenReturnUltraComfortReviews() {
        // given
        Long productId = 1L;
        Cushion targetCushioning = Cushion.VERY_SOFT;

        Review ultraSoftReview = Review.builder()
                .reviewId(1L)
                .content("쿠션이 매우 부드러워서 구름 위를 걷는 느낌")
                .cushioning(Cushion.VERY_SOFT)
                .sizeFit(SizeFit.PERFECT)
                .stability(Stability.NORMAL)
                .status(ReviewStatus.ACTIVE)
                .build();

        List<Review> reviews = List.of(ultraSoftReview);
        when(reviewRepository.findByProductIdAndCushioningAndStatus(productId, targetCushioning, ReviewStatus.ACTIVE))
                .thenReturn(reviews);

        // ReviewImageRepository Mock 설정 추가
        when(reviewImageRepository.findByReviewIdOrderByImageOrder(any())).thenReturn(new ArrayList<>());

        // when
        List<ReviewDetailResponse> responses = reviewService.getReviewsByCushioning(productId, targetCushioning);

        // then
        assertEquals(1, responses.size());
        assertEquals(Cushion.VERY_SOFT, responses.get(0).getCushioning());
        assertTrue(responses.get(0).getContent().contains("구름 위를 걷는"));
    }

    // RE-03: 리뷰 상세 조회
    @Test
    @DisplayName("Given existing review id_When get review detail_Then return 5-level characteristics")
    void givenExistingReviewId_whenGetReviewDetail_thenReturn5LevelCharacteristics() {
        // given
        Long reviewId = 1L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // ReviewImageRepository Mock 설정 추가
        when(reviewImageRepository.findByReviewIdOrderByImageOrder(reviewId)).thenReturn(new ArrayList<>());

        // when
        ReviewDetailResponse response = reviewService.getReviewDetail(reviewId);

        // then
        assertNotNull(response);
        assertEquals(reviewId, response.getReviewId());
        assertEquals(SizeFit.PERFECT, response.getSizeFit());
        assertEquals(Cushion.VERY_SOFT, response.getCushioning());
        assertEquals(Stability.VERY_STABLE, response.getStability());
    }

    // RE-04: 시간 경과에 따른 특성 변화 업데이트
    @Test
    @DisplayName("Given wearing time effect_When update review characteristics_Then reflect changes")
    void givenWearingTimeEffect_whenUpdateReviewCharacteristics_thenReflectChanges() {
        // given
        Long reviewId = 1L;
        ReviewUpdateRequest updateRequest = ReviewUpdateRequest.builder()
                .content("한 달 신어보니 처음과 달라졌어요. 늘어나서 커지고 쿠션도 주저앉았네요")
                .rating(3)
                .sizeFit(SizeFit.BIG)      // 늘어나서 커짐
                .cushioning(Cushion.FIRM)  // 쿠션이 주저앉아서 단단해짐
                .stability(Stability.UNSTABLE) // 안정성도 떨어짐
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // when
        assertDoesNotThrow(() -> {
            reviewService.updateReview(reviewId, updateRequest);
        });

        // then
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }
}