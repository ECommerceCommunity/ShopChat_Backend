package com.cMall.feedShop.review.application.dto.request;

import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.Stability;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewUpdateRequest {

    @Size(max = 100, message = "리뷰 제목은 100자를 초과할 수 없습니다")
    private String reviewTitle;

    @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다")
    private Integer rating;

    @Size(max = 1000, message = "리뷰 내용은 1000자를 초과할 수 없습니다")
    private String content;

    private SizeFit sizeFit;

    private Cushion cushioning;

    private Stability stability;

    @Size(max = 5, message = "이미지는 최대 5개까지 업로드 가능합니다")
    private List<String> imageUrls;
}