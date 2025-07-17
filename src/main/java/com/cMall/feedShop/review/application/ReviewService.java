package com.cMall.feedShop.review.application;

import com.cMall.feedShop.review.application.dto.request.ReviewCreateRequest;
import com.cMall.feedShop.review.application.dto.request.ReviewUpdateRequest;
import com.cMall.feedShop.review.application.dto.response.ReviewCreateResponse;
import com.cMall.feedShop.review.application.dto.response.ReviewDetailResponse;
import com.cMall.feedShop.review.application.dto.response.ReviewSummaryResponse;
import com.cMall.feedShop.review.application.dto.response.ProductReviewSummaryResponse;
import com.cMall.feedShop.review.domain.entity.*;
import com.cMall.feedShop.review.domain.repository.ReviewRepository;
import com.cMall.feedShop.review.domain.repository.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    @Transactional
    public ReviewCreateResponse createReview(ReviewCreateRequest request) {
        // 1. 이미 리뷰를 작성했는지 확인
        if (reviewRepository.existsByUserIdAndProductIdAndStatusActive(request.getUserId(), request.getProductId())) {
            throw new IllegalArgumentException("이미 해당 상품에 대한 리뷰를 작성하셨습니다.");
        }

        // 2. 리뷰 엔티티 생성 (모두 enum 사용)
        Review review = Review.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .reviewTitle(request.getReviewTitle())
                .rating(request.getRating())
                .content(request.getContent())
                .sizeFit(request.getSizeFit())      // enum 그대로
                .cushioning(request.getCushioning()) // enum 그대로
                .stability(request.getStability())   // enum 그대로
                .status(ReviewStatus.ACTIVE)
                .build();

        // 3. 리뷰 저장
        Review savedReview = reviewRepository.save(review);

        // 4. 이미지가 있다면 저장
        List<String> imageUrls = request.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (int i = 0; i < imageUrls.size(); i++) {
                ReviewImage reviewImage = ReviewImage.builder()
                        .reviewId(savedReview.getReviewId())
                        .imageUrl(imageUrls.get(i))
                        .imageOrder(i + 1)
                        .build();
                reviewImageRepository.save(reviewImage);
            }
        }

        // 5. 응답 객체 생성 (모두 enum 그대로)
        return ReviewCreateResponse.builder()
                .reviewId(savedReview.getReviewId())
                .productId(savedReview.getProductId())
                .userId(savedReview.getUserId())
                .reviewTitle(savedReview.getReviewTitle())
                .rating(savedReview.getRating())
                .content(savedReview.getContent())
                .sizeFit(savedReview.getSizeFit())      // enum 그대로
                .cushioning(savedReview.getCushioning()) // enum 그대로
                .stability(savedReview.getStability())   // enum 그대로
                .imageUrls(imageUrls)
                .createdAt(savedReview.getCreatedAt())
                .build();
    }

    public ProductReviewSummaryResponse getProductReviews(Long productId, Pageable pageable) {
        // 1. 상품의 리뷰 목록 조회
        Page<Review> reviewPage = reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.ACTIVE, pageable);

        // 2. 평균 평점 조회
        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);

        // 3. 총 리뷰 수 조회
        Long totalReviewCount = reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.ACTIVE);

        // 4. 최근 리뷰들을 기존 ReviewSummaryResponse로 변환
        List<ReviewSummaryResponse> recentReviews = reviewPage.getContent().stream()
                .map(this::convertToSummaryResponse)
                .collect(Collectors.toList());

        // 5. 평점 분포 계산 (간단한 버전)
        ProductReviewSummaryResponse.RatingDistribution ratingDistribution = ProductReviewSummaryResponse.RatingDistribution.builder()
                .fiveStar(0L)
                .fourStar(0L)
                .threeStar(0L)
                .twoStar(0L)
                .oneStar(0L)
                .build();

        return ProductReviewSummaryResponse.builder()
                .productId(productId)
                .totalReviews(totalReviewCount)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .ratingDistribution(ratingDistribution)
                .mostCommonSizeFit("보통") // 임시값
                .recentReviews(recentReviews)
                .build();
    }

    public ReviewDetailResponse getReviewDetail(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        return convertToDetailResponse(review);
    }

    public Page<ReviewDetailResponse> getUserReviews(Long userId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findByUserIdAndStatus(userId, ReviewStatus.ACTIVE, pageable);

        return reviewPage.map(this::convertToDetailResponse);
    }

    @Transactional
    public void updateReview(Long reviewId, ReviewUpdateRequest request) {
        // 1. 리뷰 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // 2. 리뷰 업데이트
        if (request.getReviewTitle() != null) {
            review.updateTitle(request.getReviewTitle());
        }

        if (request.getContent() != null) {
            review.updateContent(request.getContent());
        }

        if (request.getRating() != null) {
            review.updateRating(request.getRating());
        }

        if (request.getSizeFit() != null) {
            review.updateSizeFit(request.getSizeFit());
        }

        if (request.getCushioning() != null) {
            review.updateCushioning(request.getCushioning());
        }

        if (request.getStability() != null) {
            review.updateStability(request.getStability());
        }

        // 3. 저장
        reviewRepository.save(review);
    }

    private ReviewDetailResponse convertToDetailResponse(Review review) {
        // 리뷰의 이미지들 조회
        List<ReviewImage> images = reviewImageRepository.findByReviewIdOrderByImageOrder(review.getReviewId());
        List<String> imageUrls = images.stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());

        return ReviewDetailResponse.builder()
                .reviewId(review.getReviewId())
                .productId(review.getProductId())
                .userId(review.getUserId())
                .userName("사용자" + review.getUserId()) // 실제로는 User 엔티티에서 조회
                .reviewTitle(review.getReviewTitle())
                .rating(review.getRating())
                .content(review.getContent())
                .sizeFit(review.getSizeFit())      // enum 그대로
                .cushioning(review.getCushioning()) // enum 그대로
                .stability(review.getStability())   // enum 그대로
                .imageUrls(imageUrls)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    private ReviewSummaryResponse convertToSummaryResponse(Review review) {
        return ReviewSummaryResponse.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUserId())
                .productId(review.getProductId())
                .reviewTitle(review.getReviewTitle())
                .content(review.getContent())
                .rating(review.getRating())
                .sizeFit(review.getSizeFit())      // enum 그대로
                .cushioning(review.getCushioning()) // enum 그대로
                .stability(review.getStability())   // enum 그대로
                .createdAt(review.getCreatedAt())
                .images(new ArrayList<>()) // 빈 리스트로 초기화
                .build();
    }

    /**
     * 특정 상품의 사이즈 핏별 리뷰 조회
     */
    public List<ReviewDetailResponse> getReviewsBySizeFit(Long productId, SizeFit sizeFit) {
        List<Review> reviews = reviewRepository.findByProductIdAndSizeFitAndStatus(
                productId, sizeFit, ReviewStatus.ACTIVE
        );

        return reviews.stream()
                .map(this::convertToDetailResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 상품의 쿠셔닝별 리뷰 조회
     */
    public List<ReviewDetailResponse> getReviewsByCushioning(Long productId, Cushion cushioning) {
        List<Review> reviews = reviewRepository.findByProductIdAndCushioningAndStatus(
                productId, cushioning, ReviewStatus.ACTIVE
        );

        return reviews.stream()
                .map(this::convertToDetailResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 상품의 안정성별 리뷰 조회
     */
    public List<ReviewDetailResponse> getReviewsByStability(Long productId, Stability stability) {
        List<Review> reviews = reviewRepository.findByProductIdAndStabilityAndStatus(
                productId, stability, ReviewStatus.ACTIVE
        );

        return reviews.stream()
                .map(review -> convertToDetailResponse(review))
                .collect(Collectors.toList());

    }

}