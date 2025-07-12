package com.cMall.feedShop.review.application;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;
import com.cMall.feedShop.review.application.dto.request.ReviewRequest;
import com.cMall.feedShop.review.application.dto.response.ReviewResponse;
import com.cMall.feedShop.review.domain.Review;
import com.cMall.feedShop.review.domain.Review.ReviewStatus;
import com.cMall.feedShop.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    
    /**
     * 리뷰 생성
     */
    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        log.info("리뷰 생성 요청 - 사용자: {}, 상품: {}", request.getUserId(), request.getProductId());
        
        // 중복 리뷰 확인
        if (reviewRepository.existsByUserIdAndProductIdAndStatus(
                request.getUserId(), request.getProductId(), ReviewStatus.ACTIVE)) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }
        
        // 리뷰 생성
        Review review = request.toEntity();
        Review savedReview = reviewRepository.save(review);
        
        log.info("리뷰 생성 완료 - ID: {}", savedReview.getReviewId());
        return ReviewResponse.from(savedReview);
    }
    
    /**
     * 리뷰 수정
     */
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        log.info("리뷰 수정 요청 - ID: {}, 사용자: {}", reviewId, request.getUserId());
        
        Review review = reviewRepository.findActiveReviewByReviewId(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        
        // 작성자 확인
        if (!review.isOwnedBy(request.getUserId())) {
            throw new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED);
        }
        
        // 리뷰 수정
        review.updateTitle(request.getReviewTitle());
        review.updateContent(request.getContent());
        review.updateRating(request.getRating());
        
        log.info("리뷰 수정 완료 - ID: {}", reviewId);
        return ReviewResponse.from(review);
    }
    
    /**
     * 리뷰 삭제 (논리삭제)
     */
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        log.info("리뷰 삭제 요청 - ID: {}, 사용자: {}", reviewId, userId);
        
        Review review = reviewRepository.findActiveReviewByReviewId(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        
        // 작성자 확인
        if (!review.isOwnedBy(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED);
        }
        
        review.delete();
        log.info("리뷰 삭제 완료 - ID: {}", reviewId);
    }
    
    /**
     * 리뷰 비활성화
     */
    @Transactional
    public void deactivateReview(Long reviewId, Long userId) {
        log.info("리뷰 비활성화 요청 - ID: {}, 사용자: {}", reviewId, userId);
        
        Review review = reviewRepository.findActiveReviewByReviewId(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        
        // 작성자 확인
        if (!review.isOwnedBy(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED);
        }
        
        review.deactivate();
        log.info("리뷰 비활성화 완료 - ID: {}", reviewId);
    }
    
    /**
     * 리뷰 단건 조회
     */
    public ReviewResponse getReview(Long reviewId) {
        Review review = reviewRepository.findActiveReviewByReviewId(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        
        return ReviewResponse.from(review);
    }
    
    /**
     * 상품별 리뷰 목록 조회
     */
    public Page<ReviewResponse> getReviewsByProductId(Long productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findActiveReviewsByProductId(productId, pageable);
        return reviews.map(ReviewResponse::from);
    }
    
    /**
     * 상품별 리뷰 목록 조회 (요약버전)
     */
    public Page<ReviewResponse> getReviewsSummaryByProductId(Long productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findActiveReviewsByProductId(productId, pageable);
        return reviews.map(ReviewResponse::summaryFrom);
    }
    
    /**
     * 사용자별 리뷰 목록 조회
     */
    public Page<ReviewResponse> getReviewsByUserId(Long userId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findActiveReviewsByUserId(userId, pageable);
        return reviews.map(ReviewResponse::from);
    }
    
    /**
     * 필터링된 리뷰 목록 조회
     */
    public Page<ReviewResponse> getReviewsWithFilters(
            Long productId, 
            Integer minRating, 
            Integer maxRating, 
            String keyword,
            Pageable pageable) {
        
        Page<Review> reviews = reviewRepository.findReviewsWithFilters(
                productId, minRating, maxRating, keyword, pageable);
        return reviews.map(ReviewResponse::from);
    }
    
    /**
     * 상품 평균 평점 조회
     */
    public Double getAverageRating(Long productId) {
        Double rating = reviewRepository.getAverageRatingByProductId(productId);
        return rating != null ? Math.round(rating * 10.0) / 10.0 : 0.0;
    }
    
    /**
     * 상품 리뷰 개수 조회
     */
    public Long getReviewCount(Long productId) {
        return reviewRepository.countActiveReviewsByProductId(productId);
    }
}