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
public class ReviewSummaryResponse {
    private Long reviewId;
    private Long userId;
    private Long productId;
    private String reviewTitle;
    private String content;
    private Integer rating;
    private SizeFit sizeFit;
    private Cushion cushioning;
    private Stability stability;
    private LocalDateTime createdAt;
    private List<ReviewImageResponse> images; // SPRINT2에서 사용, 지금은 빈 리스트
}