package com.cMall.feedShop.review.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.Stability;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "review_title", length = 100)
    private String reviewTitle;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "content", length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "size_fit")
    private SizeFit sizeFit;

    @Enumerated(EnumType.STRING)
    @Column(name = "cushion")  // DB 컬럼명에 맞춤
    private Cushion cushioning;  // Java 필드명은 그대로

    @Enumerated(EnumType.STRING)
    @Column(name = "stability")
    private Stability stability;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // id 편의 메서드 추가 (테스트 호환성을 위해)
    public Long getId() {
        return this.reviewId;
    }

    // 비즈니스 메서드
    public void updateTitle(String reviewTitle) {
        validateTitle(reviewTitle);
        this.reviewTitle = reviewTitle;
    }

    public void updateContent(String content) {
        validateContent(content);
        this.content = content;
    }

    public void updateRating(Integer rating) {
        validateRating(rating);
        this.rating = rating;
    }

    public void updateSizeFit(SizeFit sizeFit) {
        this.sizeFit = sizeFit;
    }

    public void updateCushioning(Cushion cushioning) {
        this.cushioning = cushioning;
    }

    public void updateStability(Stability stability) {
        this.stability = stability;
    }

    public void delete() {
        this.status = ReviewStatus.DELETED;
    }

    public void restore() {
        this.status = ReviewStatus.ACTIVE;
    }

    public void hide() {
        this.status = ReviewStatus.HIDDEN;
    }

    public boolean isActive() {
        return this.status == ReviewStatus.ACTIVE;
    }

    public boolean isDeleted() {
        return this.status == ReviewStatus.DELETED;
    }

    public boolean isHidden() {
        return this.status == ReviewStatus.HIDDEN;
    }

    // 검증 메서드들
    public static void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1점에서 5점 사이여야 합니다.");
        }
    }

    public static void validateContent(String content) {
        if (content != null && content.length() > 1000) {
            throw new IllegalArgumentException("리뷰 내용은 1000자를 초과할 수 없습니다.");
        }
    }

    public static void validateTitle(String title) {
        if (title != null && title.length() > 100) {
            throw new IllegalArgumentException("리뷰 제목은 100자를 초과할 수 없습니다.");
        }
    }

    // getter 메서드 (Lombok이 생성하지만 명시적으로 필요한 경우)
    public String getReviewTitle() {
        return this.reviewTitle;
    }
}