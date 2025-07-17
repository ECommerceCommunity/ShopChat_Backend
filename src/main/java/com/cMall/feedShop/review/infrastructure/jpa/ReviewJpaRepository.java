package com.cMall.feedShop.review.infrastructure.jpa;

import com.cMall.feedShop.review.domain.entity.Review;
import com.cMall.feedShop.review.domain.entity.ReviewStatus;
import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.Stability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

    // 상품별 리뷰 조회 (상태별, 생성일 내림차순)
    Page<Review> findByProductIdAndStatusOrderByCreatedAtDesc(
            Long productId,
            ReviewStatus status,
            Pageable pageable
    );

    // 사용자별 리뷰 조회 (상태별, 생성일 내림차순)
    Page<Review> findByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId,
            ReviewStatus status,
            Pageable pageable
    );

    // 상품별 리뷰 개수 조회
    Long countByProductIdAndStatus(Long productId, ReviewStatus status);

    // 평점별 개수 조회
    Long countByProductIdAndStatusAndRating(Long productId, ReviewStatus status, Integer rating);

    // 리뷰 ID로 조회 (삭제되지 않은 것만)
    @Query("SELECT r FROM Review r WHERE r.reviewId = :reviewId AND r.status != 'DELETED'")
    Optional<Review> findByReviewIdAndStatusNotDeleted(@Param("reviewId") Long reviewId);

    // 사용자가 해당 상품에 대해 활성 리뷰를 작성했는지 확인
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.userId = :userId AND r.productId = :productId AND r.status = 'ACTIVE'")
    boolean existsByUserIdAndProductIdAndStatusActive(@Param("userId") Long userId, @Param("productId") Long productId);

    // 리뷰 ID와 사용자 ID로 소유권 확인
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.reviewId = :reviewId AND r.userId = :userId")
    boolean existsByReviewIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    // 상품별 평균 평점 조회
    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.productId = :productId AND r.status = 'ACTIVE'")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    // 사이즈 핏별 필터링
    @Query("SELECT r FROM Review r WHERE r.productId = :productId AND r.sizeFit = :sizeFit AND r.status = :status")
    List<Review> findByProductIdAndSizeFitAndStatus(
            @Param("productId") Long productId,
            @Param("sizeFit") SizeFit sizeFit,
            @Param("status") ReviewStatus status
    );

    // 쿠셔닝별 필터링
    @Query("SELECT r FROM Review r WHERE r.productId = :productId AND r.cushioning = :cushioning AND r.status = :status")
    List<Review> findByProductIdAndCushioningAndStatus(
            @Param("productId") Long productId,
            @Param("cushioning") Cushion cushioning,
            @Param("status") ReviewStatus status
    );

    // 안정성별 필터링
    @Query("SELECT r FROM Review r WHERE r.productId = :productId AND r.stability = :stability AND r.status = :status")
    List<Review> findByProductIdAndStabilityAndStatus(
            @Param("productId") Long productId,
            @Param("stability") Stability stability,
            @Param("status") ReviewStatus status
    );

    // 쿠셔닝별 평균 평점 조회
    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.cushioning = :cushioning AND r.status = :status")
    Double findAverageRatingByCushioning(@Param("cushioning") Cushion cushioning, @Param("status") ReviewStatus status);

    // 사이즈 핏별 평균 평점 조회
    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.sizeFit = :sizeFit AND r.status = :status")
    Double findAverageRatingBySizeFit(@Param("sizeFit") SizeFit sizeFit, @Param("status") ReviewStatus status);

    // 안정성별 평균 평점 조회
    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.stability = :stability AND r.status = :status")
    Double findAverageRatingByStability(@Param("stability") Stability stability, @Param("status") ReviewStatus status);
}