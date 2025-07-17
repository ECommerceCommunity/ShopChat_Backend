package com.cMall.feedShop.review.application.dto.response;

import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Stability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDetailResponse {
    
    private Long reviewId;
    private Long productId;
    private Long userId;
    private String userName;
    private String reviewTitle;
    private Integer rating;
    private String content;
    private SizeFit sizeFit;
    private Cushion cushioning;
    private Stability stability;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}