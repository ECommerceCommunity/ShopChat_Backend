package com.cMall.feedShop.review.domain.entity;

public enum ReviewStatus {
    ACTIVE("활성"),
    DELETED("삭제됨"),
    HIDDEN("숨김");
    
    private final String description;
    
    ReviewStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}