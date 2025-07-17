package com.cMall.feedShop.review.application.dto.response;

import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Stability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateResponse {
    private Long reviewId;
    private Long userId;
    private Long productId;
    private Long orderItemId;
    private String reviewTitle;
    private String content;
    private Integer rating;
    private SizeFit sizeFit;
    private Cushion cushioning;
    private Stability stability;
    private Integer points;
    private Integer reportCount;
    private Boolean isBlinded;
    private Boolean hasDetailedContent;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> imageUrls;
    private LocalDateTime deletedAt;
}