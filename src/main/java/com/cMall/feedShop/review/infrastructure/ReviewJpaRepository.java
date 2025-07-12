package com.cMall.feedShop.review.infrastructure;

import com.cMall.feedShop.review.domain.Review;
import com.cMall.feedShop.review.domain.Review.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
    
    // 활성화된 리뷰 조회
    Optional<Review> findByReviewIdAndStatus(Long reviewId, ReviewStatus status);
    
    // 상품별 활성화된 리뷰 조회
    Page<Review> findByProductIdAndStatusOrderByCreatedAtDesc(Long productId, ReviewStatus status, Pageable pageable);
    
    // 사용자별 활성화된 리뷰 조회
    Page<Review> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, ReviewStatus status, Pageable pageable);
    
    // 통계 쿼리
    Long countByProductIdAndStatus(Long productId, ReviewStatus status);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productId = :productId AND r.status = :status")
    Double findAverageRatingByProductIdAndStatus(@Param("productId") Long productId, @Param("status") ReviewStatus status);
    
    // 중복 리뷰 확인
    boolean existsByUserIdAndProductIdAndStatus(Long userId, Long productId, ReviewStatus status);
    Optional<Review> findByUserIdAndProductIdAndStatus(Long userId, Long productId, ReviewStatus status);
    
    // 복합 필터 검색
    @Query("SELECT r FROM Review r WHERE r.productId = :productId " +
           "AND r.status = 'ACTIVE' " +
           "AND (:minRating IS NULL OR r.rating >= :minRating) " +
           "AND (:maxRating IS NULL OR r.rating <= :maxRating) " +
           "AND (:keyword IS NULL OR r.content LIKE %:keyword% OR r.reviewTitle LIKE %:keyword%) " +
           "ORDER BY r.createdAt DESC")
    Page<Review> findReviewsWithFilters(
            @Param("productId") Long productId,
            @Param("minRating") Integer minRating,
            @Param("maxRating") Integer maxRating,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}