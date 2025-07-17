package com.cMall.feedShop.review.domain.repository;

import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Stability;
import com.cMall.feedShop.review.domain.entity.Review;
import com.cMall.feedShop.review.domain.entity.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 도메인 Repository 인터페이스
 * 순수한 도메인 개념 - 기술에 의존하지 않음
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 리뷰 저장
     */
    Review save(Review review);

    /**
     * ID로 리뷰 조회
     */
    Optional<Review> findById(Long reviewId);

    /**
     * 리뷰 삭제 (물리적 삭제)
     */
    void deleteById(Long reviewId);

    /**
     * 모든 리뷰 조회 (관리자용)
     */
    Page<Review> findAll(Pageable pageable);

    /**
     * 상품별 활성 리뷰 목록 조회 (페이징)
     */
    Page<Review> findByProductIdAndStatus(Long productId, ReviewStatus status, Pageable pageable);

    /**
     * 사용자별 리뷰 목록 조회 (페이징)
     */
    Page<Review> findByUserIdAndStatus(Long userId, ReviewStatus status, Pageable pageable);

    /**
     * 상품별 활성 리뷰 개수 조회
     */
    Long countByProductIdAndStatus(Long productId, ReviewStatus status);

    /**
     * 평점별 개수 조회
     */
    Long countByProductIdAndStatusAndRating(Long productId, ReviewStatus status, Integer rating);

    // ===== @Query가 필요한 메서드들 =====

    /**
     * 중복 리뷰 존재 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.userId = :userId AND r.productId = :productId AND r.status = com.cMall.feedShop.review.domain.entity.ReviewStatus.ACTIVE")
    boolean existsByUserIdAndProductIdAndStatusActive(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 상품별 평균 평점 조회
     */
    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.productId = :productId AND r.status = com.cMall.feedShop.review.domain.entity.ReviewStatus.ACTIVE")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    /**
     * 쿠셔닝별 평균 평점 조회
     */
    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.cushioning = :cushioning AND r.status = :status")
    Double findAverageRatingByCushioning(@Param("cushioning") Cushion cushioning, @Param("status") ReviewStatus status);

    /**
     * 사이즈 핏별 평균 평점 조회
     */
    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.sizeFit = :sizeFit AND r.status = :status")
    Double findAverageRatingBySizeFit(@Param("sizeFit") SizeFit sizeFit, @Param("status") ReviewStatus status);

    /**
     * 안정성별 평균 평점 조회
     */
    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.stability = :stability AND r.status = :status")
    Double findAverageRatingByStability(@Param("stability") Stability stability, @Param("status") ReviewStatus status);

    /**
     * 특정 조건으로 리뷰 필터링 조회 - 사이즈 핏별
     */
    @Query("SELECT r FROM Review r WHERE r.productId = :productId AND r.sizeFit = :sizeFit AND r.status = :status")
    List<Review> findByProductIdAndSizeFitAndStatus(@Param("productId") Long productId,
                                                    @Param("sizeFit") SizeFit sizeFit,
                                                    @Param("status") ReviewStatus status);

    /**
     * 특정 조건으로 리뷰 필터링 조회 - 쿠셔닝별
     */
    @Query("SELECT r FROM Review r WHERE r.productId = :productId AND r.cushioning = :cushioning AND r.status = :status")
    List<Review> findByProductIdAndCushioningAndStatus(@Param("productId") Long productId,
                                                       @Param("cushioning") Cushion cushioning,
                                                       @Param("status") ReviewStatus status);

    /**
     * 특정 조건으로 리뷰 필터링 조회 - 안정성별
     */
    @Query("SELECT r FROM Review r WHERE r.productId = :productId AND r.stability = :stability AND r.status = :status")
    List<Review> findByProductIdAndStabilityAndStatus(@Param("productId") Long productId,
                                                      @Param("stability") Stability stability,
                                                      @Param("status") ReviewStatus status);
}