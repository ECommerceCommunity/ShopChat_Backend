package com.cMall.feedShop.review.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class SizeFitTest {

    @Test
    @DisplayName("Given 5-level size fit values_When get all values_Then return complete enum set")
    void given5LevelSizeFitValues_whenGetAllValues_thenReturnCompleteEnumSet() {
        // given & when
        SizeFit[] allSizeFits = SizeFit.values();

        // then
        assertEquals(5, allSizeFits.length); // 정확히 5개

        // 신발 사이즈 핏 5단계가 모두 존재하는지 확인
        boolean hasVerySmall = false, hasSmall = false, hasPerfect = false,
                hasBig = false, hasVeryBig = false;

        for (SizeFit sizeFit : allSizeFits) {
            switch (sizeFit) {
                case VERY_SMALL: hasVerySmall = true; break; // 매우 작음
                case SMALL: hasSmall = true; break;          // 작음
                case PERFECT: hasPerfect = true; break;      // 딱 맞음
                case BIG: hasBig = true; break;              // 큼
                case VERY_BIG: hasVeryBig = true; break;     // 매우 큼
            }
        }

        assertTrue(hasVerySmall && hasSmall && hasPerfect && hasBig && hasVeryBig);
    }

    @Test
    @DisplayName("Given size fit comparison_When check fit level_Then order correctly")
    void givenSizeFitComparison_whenCheckFitLevel_thenOrderCorrectly() {
        // given & when & then
        // 신발 사이즈 순서: 매우 작음 < 작음 < 딱 맞음 < 큼 < 매우 큼
        assertTrue(SizeFit.VERY_SMALL.ordinal() < SizeFit.SMALL.ordinal());
        assertTrue(SizeFit.SMALL.ordinal() < SizeFit.PERFECT.ordinal());
        assertTrue(SizeFit.PERFECT.ordinal() < SizeFit.BIG.ordinal());
        assertTrue(SizeFit.BIG.ordinal() < SizeFit.VERY_BIG.ordinal());
    }

    @Test
    @DisplayName("Given size fit extremes_When evaluate comfort_Then return appropriate assessment")
    void givenSizeFitExtremes_whenEvaluateComfort_thenReturnAppropriateAssessment() {
        // given & when & then
        assertEquals("VERY_SMALL", SizeFit.VERY_SMALL.name()); // 발가락 심하게 눌림
        assertEquals("PERFECT", SizeFit.PERFECT.name());       // 이상적인 핏
        assertEquals("VERY_BIG", SizeFit.VERY_BIG.name());     // 발이 헐렁거림
    }
}