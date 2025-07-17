package com.cMall.feedShop.review.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImageResponse {
    private Long imageId;
    private Long reviewsId;
    private String imageUrl;
    private Integer imageOrder;
    private LocalDateTime createdAt;
}