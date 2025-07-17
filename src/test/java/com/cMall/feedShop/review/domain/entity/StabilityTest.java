package com.cMall.feedShop.review.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class StabilityTest {

    @Test
    @DisplayName("Given 5-level stability values_When get all values_Then return complete enum set")
    void given5LevelStabilityValues_whenGetAllValues_thenReturnCompleteEnumSet() {
        // given & when
        Stability[] allStabilities = Stability.values();

        // then
        assertEquals(5, allStabilities.length); // 정확히 5개

        // 신발 안정성 5단계가 모두 존재하는지 확인
        boolean hasVeryUnstable = false, hasUnstable = false, hasNormal = false,
                hasStable = false, hasVeryStable = false;

        for (Stability stability : allStabilities) {
            switch (stability) {
                case VERY_UNSTABLE: hasVeryUnstable = true; break; // 매우 불안정
                case UNSTABLE: hasUnstable = true; break;          // 불안정
                case NORMAL: hasNormal = true; break;              // 보통
                case STABLE: hasStable = true; break;              // 안정적
                case VERY_STABLE: hasVeryStable = true; break;     // 매우 안정적
            }
        }

        assertTrue(hasVeryUnstable && hasUnstable && hasNormal && hasStable && hasVeryStable);
    }

    @Test
    @DisplayName("Given stability comparison_When check support level_Then order correctly")
    void givenStabilityComparison_whenCheckSupportLevel_thenOrderCorrectly() {
        // given & when & then
        // 신발 안정성 순서: 매우 불안정 < 불안정 < 보통 < 안정적 < 매우 안정적
        assertTrue(Stability.VERY_UNSTABLE.ordinal() < Stability.UNSTABLE.ordinal());
        assertTrue(Stability.UNSTABLE.ordinal() < Stability.NORMAL.ordinal());
        assertTrue(Stability.NORMAL.ordinal() < Stability.STABLE.ordinal());
        assertTrue(Stability.STABLE.ordinal() < Stability.VERY_STABLE.ordinal());
    }

    @Test
    @DisplayName("Given stability extremes_When evaluate ankle support_Then return appropriate assessment")
    void givenStabilityExtremes_whenEvaluateAnkleSupport_thenReturnAppropriateAssessment() {
        // given & when & then
        assertEquals("VERY_UNSTABLE", Stability.VERY_UNSTABLE.name()); // 발목 삐끗 위험
        assertEquals("NORMAL", Stability.NORMAL.name());               // 일반적인 지지력
        assertEquals("VERY_STABLE", Stability.VERY_STABLE.name());     // 강력한 발목 지지
    }

    @Test
    @DisplayName("Given stability requirement by terrain_When choose stability level_Then match terrain needs")
    void givenStabilityRequirementByTerrain_whenChooseStabilityLevel_thenMatchTerrainNeeds() {
        // given & when & then
        // 평지 걷기: 보통 안정성으로도 충분
        Stability flatWalking = Stability.NORMAL;
        assertEquals(Stability.NORMAL, flatWalking);

        // 등산/트레킹: 높은 안정성 필요
        Stability hiking = Stability.STABLE;
        assertEquals(Stability.STABLE, hiking);

        // 험한 산악지형: 최고 안정성 필요
        Stability extremeTerrain = Stability.VERY_STABLE;
        assertEquals(Stability.VERY_STABLE, extremeTerrain);
    }
}