package com.cMall.feedShop.review.infrastructure;

import com.cMall.feedShop.review.domain.Review;
import com.cMall.feedShop.review.domain.Review.ReviewStatus;
import com.cMall.feedShop.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {
    
    private final ReviewJpaRepository jpaRepository;
    
    @Override
    public Review save(Review review) {
        return jpaRepository.save(review);
    }
    
    @Override
    public Optional<Review> findByReviewId(Long reviewId) {
        return jpaRepository.findById(reviewId);
    }
    
    @Override
    public void deleteByReviewId(Long reviewId) {
        jpaRepository.deleteById(reviewId);
    }
    
    @Override
    public boolean existsByReviewId(Long reviewId) {
        return jpaRepository.existsById(reviewId);
    }
    
    @Override
    public Optional<Review> findActiveReviewByReviewId(Long reviewId) {
        return jpaRepository.findByReviewIdAndStatus(reviewId, ReviewStatus.ACTIVE);
    }
    
    @Override
    public Page<Review> findActiveReviewsByProductId(Long productId, Pageable pageable) {
        return jpaRepository.findByProductIdAndStatusOrderByCreatedAtDesc(productId, ReviewStatus.ACTIVE, pageable);
    }
    
    @Override
    public Page<Review> findActiveReviewsByUserId(Long userId, Pageable pageable) {
        return jpaRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, ReviewStatus.ACTIVE, pageable);
    }
    
    @Override
    public Long countActiveReviewsByProductId(Long productId) {
        return jpaRepository.countByProductIdAndStatus(productId, ReviewStatus.ACTIVE);
    }
    
    @Override
    public Double getAverageRatingByProductId(Long productId) {
        return jpaRepository.findAverageRatingByProductIdAndStatus(productId, ReviewStatus.ACTIVE);
    }
    
    @Override
    public Page<Review> findReviewsWithFilters(Long productId, Integer minRating, Integer maxRating, String keyword, Pageable pageable) {
        return jpaRepository.findReviewsWithFilters(productId, minRating, maxRating, keyword, pageable);
    }
    
    @Override
    public boolean existsByUserIdAndProductIdAndStatus(Long userId, Long productId, ReviewStatus status) {
        return jpaRepository.existsByUserIdAndProductIdAndStatus(userId, productId, status);
    }
    
    @Override
    public Optional<Review> findByUserIdAndProductIdAndStatus(Long userId, Long productId, ReviewStatus status) {
        return jpaRepository.findByUserIdAndProductIdAndStatus(userId, productId, status);
    }
}