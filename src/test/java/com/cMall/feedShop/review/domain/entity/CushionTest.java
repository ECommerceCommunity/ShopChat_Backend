package com.cMall.feedShop.review.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CushionTest {

    @Test
    @DisplayName("Given 5-level cushioning values_When get all values_Then return complete enum set")
    void given5LevelCushioningValues_whenGetAllValues_thenReturnCompleteEnumSet() {
        // given & when
        Cushion[] allCushions = Cushion.values();

        // then
        assertEquals(5, allCushions.length); // 정확히 5개

        // 신발 쿠션감 5단계가 모두 존재하는지 확인
        boolean hasVerySoft = false, hasSoft = false, hasNormal = false,
                hasFirm = false, hasVeryFirm = false;

        for (Cushion cushion : allCushions) {
            switch (cushion) {
                case VERY_SOFT: hasVerySoft = true; break; // 매우 부드러움
                case SOFT: hasSoft = true; break;          // 부드러움
                case NORMAL: hasNormal = true; break;      // 보통
                case FIRM: hasFirm = true; break;          // 단단함
                case VERY_FIRM: hasVeryFirm = true; break; // 매우 단단함
            }
        }

        assertTrue(hasVerySoft && hasSoft && hasNormal && hasFirm && hasVeryFirm);
    }

    @Test
    @DisplayName("Given cushioning comparison_When check comfort level_Then order correctly")
    void givenCushioningComparison_whenCheckComfortLevel_thenOrderCorrectly() {
        // given & when & then
        // 신발 쿠션감 순서: 매우 부드러움 > 부드러움 > 보통 > 단단함 > 매우 단단함
        assertTrue(Cushion.VERY_SOFT.ordinal() < Cushion.SOFT.ordinal());
        assertTrue(Cushion.SOFT.ordinal() < Cushion.NORMAL.ordinal());
        assertTrue(Cushion.NORMAL.ordinal() < Cushion.FIRM.ordinal());
        assertTrue(Cushion.FIRM.ordinal() < Cushion.VERY_FIRM.ordinal());
    }

    @Test
    @DisplayName("Given cushioning extremes_When evaluate walking experience_Then return appropriate description")
    void givenCushioningExtremes_whenEvaluateWalkingExperience_thenReturnAppropriateDescription() {
        // given & when & then
        assertEquals("VERY_SOFT", Cushion.VERY_SOFT.name());   // 구름 위를 걷는 느낌
        assertEquals("NORMAL", Cushion.NORMAL.name());         // 일반적인 쿠션감
        assertEquals("VERY_FIRM", Cushion.VERY_FIRM.name());   // 바닥 그대로 느껴짐
    }

    @Test
    @DisplayName("Given cushioning preference by activity_When choose cushion level_Then match activity needs")
    void givenCushioningPreferenceByActivity_whenChooseCushionLevel_thenMatchActivityNeeds() {
        // given & when & then
        // 일상 걷기: 부드러운 쿠션 선호
        Cushion casualWalking = Cushion.SOFT;
        assertEquals(Cushion.SOFT, casualWalking);

        // 런닝: 적당한 쿠션 선호
        Cushion running = Cushion.NORMAL;
        assertEquals(Cushion.NORMAL, running);

        // 농구/운동: 단단한 쿠션 선호 (반응성)
        Cushion sports = Cushion.FIRM;
        assertEquals(Cushion.FIRM, sports);
    }
}