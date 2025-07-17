package com.cMall.feedShop.review.domain.repository;

import com.cMall.feedShop.review.domain.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 이미지 도메인 Repository 인터페이스
 * JpaRepository를 상속하여 기본 CRUD 기능 제공
 */
@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    /**
     * 이미지 저장 (JpaRepository에서 상속)
     */
    // save() 메서드는 JpaRepository에서 제공

    /**
     * ID로 이미지 조회 (JpaRepository에서 상속)
     */
    // findById() 메서드는 JpaRepository에서 제공

    /**
     * 리뷰별 이미지 목록 조회 (순서대로)
     */
    @Query("SELECT ri FROM ReviewImage ri WHERE ri.reviewId = :reviewId ORDER BY ri.imageOrder ASC")
    List<ReviewImage> findByReviewIdOrderByImageOrder(@Param("reviewId") Long reviewId);

    /**
     * 리뷰별 이미지 개수 조회
     */
    @Query("SELECT COUNT(ri) FROM ReviewImage ri WHERE ri.reviewId = :reviewId")
    Long countByReviewId(@Param("reviewId") Long reviewId);

    /**
     * 이미지 삭제 (JpaRepository에서 상속)
     */
    // deleteById() 메서드는 JpaRepository에서 제공

    /**
     * 리뷰별 모든 이미지 삭제
     */
    @Query("DELETE FROM ReviewImage ri WHERE ri.reviewId = :reviewId")
    void deleteByReviewId(@Param("reviewId") Long reviewId);

    /**
     * 이미지 소유권 확인 - 수정된 쿼리
     */
    @Query("SELECT COUNT(ri) > 0 FROM ReviewImage ri JOIN Review r ON ri.reviewId = r.reviewId WHERE ri.imageId = :imageId AND r.userId = :userId")
    boolean existsByIdAndReviewUserId(@Param("imageId") Long imageId, @Param("userId") Long userId);
}