package com.cMall.feedShop.review.domain.repository;

import com.cMall.feedShop.review.domain.Review;
import com.cMall.feedShop.review.domain.Review.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReviewRepository {
    
    // 기본 CRUD
    Review save(Review review);
    Optional<Review> findByReviewId(Long reviewId);
    void deleteByReviewId(Long reviewId);
    boolean existsByReviewId(Long reviewId);
    
    // 활성화된 리뷰 조회
    Optional<Review> findActiveReviewByReviewId(Long reviewId);
    Page<Review> findActiveReviewsByProductId(Long productId, Pageable pageable);
    Page<Review> findActiveReviewsByUserId(Long userId, Pageable pageable);
    
    // 통계 조회
    Long countActiveReviewsByProductId(Long productId);
    Double getAverageRatingByProductId(Long productId);
    
    // 필터링 조회
    Page<Review> findReviewsWithFilters(
        Long productId, 
        Integer minRating, 
        Integer maxRating, 
        String keyword,
        Pageable pageable
    );
    
    // 사용자별 리뷰 확인
    boolean existsByUserIdAndProductIdAndStatus(Long userId, Long productId, ReviewStatus status);
    Optional<Review> findByUserIdAndProductIdAndStatus(Long userId, Long productId, ReviewStatus status);
}