package com.cMall.feedShop.review.fixture;

import com.cMall.feedShop.review.domain.entity.Review;
import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.Stability;
import com.cMall.feedShop.review.domain.entity.ReviewStatus;
import com.cMall.feedShop.review.application.dto.request.ReviewCreateRequest;

public class ReviewFixture {

    // 최고 등급 신발 리뷰
    public static ReviewCreateRequest createPremiumShoeReviewRequest() {
        return ReviewCreateRequest.builder()
                .content("최고급 신발입니다. 모든 면에서 완벽해요")
                .rating(5)
                .userId(1L)
                .productId(1L)
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .build();
    }

    // 최악 등급 신발 리뷰
    public static ReviewCreateRequest createWorstShoeReviewRequest() {
        return ReviewCreateRequest.builder()
                .content("최악의 신발입니다. 모든 게 다 별로예요")
                .rating(1)
                .userId(1L)
                .productId(1L)
                .sizeFit(SizeFit.VERY_SMALL)
                .cushioning(Cushion.VERY_FIRM)
                .stability(Stability.VERY_UNSTABLE)
                .build();
    }

    // 각 특성별 극단값 테스트용
    public static Review createReviewWithExtremeSizeFit(SizeFit sizeFit) {
        String content = switch (sizeFit) {
            case VERY_SMALL -> "발가락이 심하게 눌려요";
            case VERY_BIG -> "발이 신발 안에서 헤엄쳐요";
            default -> "사이즈 " + sizeFit.name();
        };

        return Review.builder()
                .content(content)
                .rating(sizeFit == SizeFit.PERFECT ? 5 : 2)
                .userId(1L)
                .productId(1L)
                .sizeFit(sizeFit)
                .cushioning(Cushion.NORMAL)
                .stability(Stability.NORMAL)
                .status(ReviewStatus.ACTIVE)
                .build();
    }

    public static Review createReviewWithExtremeCushioning(Cushion cushioning) {
        String content = switch (cushioning) {
            case VERY_SOFT -> "구름 위를 걷는 느낌";
            case VERY_FIRM -> "바닥을 그대로 느껴요";
            default -> "쿠션감 " + cushioning.name();
        };

        return Review.builder()
                .content(content)
                .rating(cushioning == Cushion.VERY_SOFT ? 5 : 2)
                .userId(1L)
                .productId(1L)
                .sizeFit(SizeFit.PERFECT)
                .cushioning(cushioning)
                .stability(Stability.NORMAL)
                .status(ReviewStatus.ACTIVE)
                .build();
    }

    public static Review createReviewWithExtremeStability(Stability stability) {
        String content = switch (stability) {
            case VERY_STABLE -> "발목이 완전히 고정된 느낌";
            case VERY_UNSTABLE -> "한 발짝마다 삐끗할 것 같아요";
            default -> "안정성 " + stability.name();
        };

        return Review.builder()
                .content(content)
                .rating(stability == Stability.VERY_STABLE ? 5 : 2)
                .userId(1L)
                .productId(1L)
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.NORMAL)
                .stability(stability)
                .status(ReviewStatus.ACTIVE)
                .build();
    }
}