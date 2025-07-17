/*package com.cMall.feedShop.review.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ReviewStatusTest {

    @Test
    @DisplayName("Given review status values_When get all values_Then return complete enum set")
    void givenReviewStatusValues_whenGetAllValues_thenReturnCompleteEnumSet() {
        // given & when
        ReviewStatus[] allStatuses = ReviewStatus.values();

        // then
        assertTrue(allStatuses.length >= 3); // 최소 ACTIVE, BLIND, DELETED

        // 리뷰 상태들이 존재하는지 확인
        boolean hasActive = false, hasBlind = false, hasDeleted = false;
        for (ReviewStatus status : allStatuses) {
            switch (status) {
                case ACTIVE: hasActive = true; break;   // 활성 상태
                case BLIND: hasBlind = true; break;     // 블라인드 처리
                case DELETED: hasDeleted = true; break; // 삭제됨
            }
        }

        assertTrue(hasActive && hasBlind && hasDeleted);
    }

    @Test
    @DisplayName("Given review status_When check visibility_Then return correct state")
    void givenReviewStatus_whenCheckVisibility_thenReturnCorrectState() {
        // given & when & then
        assertTrue(ReviewStatus.ACTIVE.isVisible());  // 활성 상태는 보임
        assertFalse(ReviewStatus.BLIND.isVisible());  // 블라인드는 안 보임
        assertFalse(ReviewStatus.DELETED.isVisible()); // 삭제는 안 보임
    }

    @Test
    @DisplayName("Given review moderation_When change status_Then handle workflow correctly")
    void givenReviewModeration_whenChangeStatus_thenHandleWorkflowCorrectly() {
        // given
        ReviewStatus initialStatus = ReviewStatus.ACTIVE;

        // when & then - 신고로 인한 블라인드 처리
        ReviewStatus blindStatus = ReviewStatus.BLIND;
        assertNotEquals(initialStatus, blindStatus);
        assertFalse(blindStatus.isVisible());

        // when & then - 사용자 요청으로 삭제
        ReviewStatus deletedStatus = ReviewStatus.DELETED;
        assertNotEquals(initialStatus, deletedStatus);
        assertFalse(deletedStatus.isVisible());
    }
}*/