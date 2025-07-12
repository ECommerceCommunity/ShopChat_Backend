package com.cMall.feedShop.review.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;
    
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 100)
    private String reviewTitle;
    
    @Column(nullable = false)
    private Integer rating;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // 리뷰 상태 ENUM
    public enum ReviewStatus {
        ACTIVE,     // 활성
        INACTIVE,   // 비활성
        DELETED     // 삭제
    }
    
    // 비즈니스 메서드
    public void updateContent(String content) {
        validateContent(content);
        this.content = content;
    }
    
    public void updateRating(Integer rating) {
        validateRating(rating);
        this.rating = rating;
    }
    
    public void updateTitle(String reviewTitle) {
        validateTitle(reviewTitle);
        this.reviewTitle = reviewTitle;
    }
    
    public void deactivate() {
        this.status = ReviewStatus.INACTIVE;
    }
    
    public void activate() {
        this.status = ReviewStatus.ACTIVE;
    }
    
    public void delete() {
        this.status = ReviewStatus.DELETED;
    }
    
    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }
    
    public boolean isActive() {
        return this.status == ReviewStatus.ACTIVE;
    }
    
    // 유효성 검증 (정적 메서드)
    public static void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1-5 사이의 값이어야 합니다.");
        }
    }
    
    public static void validateContent(String content) {
        if (content != null && content.length() > 1000) {
            throw new IllegalArgumentException("리뷰 내용은 1000자를 초과할 수 없습니다.");
        }
    }
    
    public static void validateTitle(String reviewTitle) {
        if (reviewTitle == null || reviewTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("리뷰 제목은 필수입니다.");
        }
        if (reviewTitle.length() > 100) {
            throw new IllegalArgumentException("리뷰 제목은 100자 이하여야 합니다.");
        }
    }
    
    // 생성 시 유효성 검증
    @PrePersist
    private void prePersist() {
        validateRating(this.rating);
        validateContent(this.content);
        validateTitle(this.reviewTitle);
    }
}