package com.cMall.feedShop.review.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    @DisplayName("Given valid shoe review data with 5-level enums_When create review_Then success")
    void givenValidShoeReviewDataWith5LevelEnums_whenCreateReview_thenSuccess() {
        // given
        String content = "정말 편한 신발입니다. 장시간 착용해도 발이 전혀 아프지 않아요";
        int rating = 5;
        Long userId = 1L;
        SizeFit sizeFit = SizeFit.PERFECT; // 딱 맞음
        Cushion cushioning = Cushion.VERY_SOFT; // 매우 부드러움
        Stability stability = Stability.VERY_STABLE; // 매우 안정적

        // when
        Review review = Review.builder()
                .content(content)
                .rating(rating)
                .userId(userId)
                .sizeFit(sizeFit)
                .cushioning(cushioning)
                .stability(stability)
                .status(ReviewStatus.ACTIVE)
                .build();

        // then
        assertNotNull(review);
        assertEquals(content, review.getContent());
        assertEquals(rating, review.getRating());
        assertEquals(userId, review.getUserId());
        assertEquals(sizeFit, review.getSizeFit());
        assertEquals(cushioning, review.getCushioning());
        assertEquals(stability, review.getStability());
        assertEquals(ReviewStatus.ACTIVE, review.getStatus());
    }

    @Test
    @DisplayName("Given extreme size fit scenarios_When create review_Then handle all 5 levels")
    void givenExtremeSizeFitScenarios_whenCreateReview_thenHandleAll5Levels() {
        // given - 매우 작음
        Review verySmallReview = Review.builder()
                .content("사이즈가 매우 작아요. 발가락이 심하게 눌려요")
                .rating(2)
                .userId(1L)
                .sizeFit(SizeFit.VERY_SMALL)
                .cushioning(Cushion.NORMAL)
                .stability(Stability.NORMAL)
                .status(ReviewStatus.ACTIVE)
                .build();

        // given - 매우 큼
        Review veryBigReview = Review.builder()
                .content("사이즈가 매우 커요. 발이 신발 안에서 헐렁거려요")
                .rating(2)
                .userId(2L)
                .sizeFit(SizeFit.VERY_BIG)
                .cushioning(Cushion.NORMAL)
                .stability(Stability.NORMAL)
                .status(ReviewStatus.ACTIVE)
                .build();

        // when & then
        assertEquals(SizeFit.VERY_SMALL, verySmallReview.getSizeFit());
        assertEquals(SizeFit.VERY_BIG, veryBigReview.getSizeFit());
        assertTrue(verySmallReview.getContent().contains("매우 작아요"));
        assertTrue(veryBigReview.getContent().contains("매우 커요"));
    }

    @Test
    @DisplayName("Given extreme cushioning levels_When create review_Then handle all 5 levels")
    void givenExtremeCushioningLevels_whenCreateReview_thenHandleAll5Levels() {
        // given - 매우 부드러움
        Review verySoftReview = Review.builder()
                .content("쿠션이 매우 부드러워서 구름 위를 걷는 느낌이에요")
                .rating(5)
                .userId(1L)
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.NORMAL)
                .status(ReviewStatus.ACTIVE)
                .build();

        // given - 매우 단단함
        Review veryFirmReview = Review.builder()
                .content("쿠션이 매우 단단해서 바닥을 그대로 느끼는 느낌")
                .rating(2)
                .userId(2L)
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_FIRM)
                .stability(Stability.NORMAL)
                .status(ReviewStatus.ACTIVE)
                .build();

        // when & then
        assertEquals(Cushion.VERY_SOFT, verySoftReview.getCushioning());
        assertEquals(Cushion.VERY_FIRM, veryFirmReview.getCushioning());
        assertTrue(verySoftReview.getContent().contains("매우 부드러워서"));
        assertTrue(veryFirmReview.getContent().contains("매우 단단해서"));
    }

    @Test
    @DisplayName("Given extreme stability levels_When create review_Then handle all 5 levels")
    void givenExtremeStabilityLevels_whenCreateReview_thenHandleAll5Levels() {
        // given - 매우 불안정
        Review veryUnstableReview = Review.builder()
                .content("발목이 매우 불안정해서 조금만 걸어도 삐끗할 것 같아요")
                .rating(1)
                .userId(1L)
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.NORMAL)
                .stability(Stability.VERY_UNSTABLE)
                .status(ReviewStatus.ACTIVE)
                .build();

        // given - 매우 안정적
        Review veryStableReview = Review.builder()
                .content("발목 지지력이 매우 안정적이어서 운동할 때 최고예요")
                .rating(5)
                .userId(2L)
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.NORMAL)
                .stability(Stability.VERY_STABLE)
                .status(ReviewStatus.ACTIVE)
                .build();

        // when & then
        assertEquals(Stability.VERY_UNSTABLE, veryUnstableReview.getStability());
        assertEquals(Stability.VERY_STABLE, veryStableReview.getStability());
        assertTrue(veryUnstableReview.getContent().contains("매우 불안정"));
        assertTrue(veryStableReview.getContent().contains("매우 안정적"));
    }

    @Test
    @DisplayName("Given shoe review update_When change characteristics_Then update successfully")
    void givenShoeReviewUpdate_whenChangeCharacteristics_thenUpdateSuccessfully() {
        // given
        Review review = createValidShoeReview();

        // when - 시간이 지나면서 느낌이 변함
        review.updateSizeFit(SizeFit.BIG); // 늘어나서 커짐
        review.updateCushioning(Cushion.FIRM); // 쿠션이 눌려서 단단해짐
        review.updateStability(Stability.UNSTABLE); // 안정성도 떨어짐

        // then
        assertEquals(SizeFit.BIG, review.getSizeFit());
        assertEquals(Cushion.FIRM, review.getCushioning());
        assertEquals(Stability.UNSTABLE, review.getStability());
    }

    private Review createValidShoeReview() {
        return Review.builder()
                .content("편안하고 스타일리시한 신발입니다")
                .rating(5)
                .userId(1L)
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.SOFT)
                .stability(Stability.STABLE)
                .status(ReviewStatus.ACTIVE)
                .build();
    }
}