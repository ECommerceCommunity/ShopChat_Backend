package com.cMall.feedShop.review.application.dto.request;

import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.Stability;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)  // toBuilder 옵션 추가
public class ReviewCreateRequest  {

    @NotNull(message = "사용자 ID는 필수입니다")
    @Positive(message = "사용자 ID는 양수여야 합니다")
    private Long userId;

    @NotNull(message = "상품 ID는 필수입니다")
    @Positive(message = "상품 ID는 양수여야 합니다")
    private Long productId;

    @Size(max = 100, message = "리뷰 제목은 100자를 초과할 수 없습니다")
    private String reviewTitle;

    @NotNull(message = "평점은 필수입니다")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다")
    private Integer rating;

    @Size(max = 1000, message = "리뷰 내용은 1000자를 초과할 수 없습니다")
    private String content;

    @NotNull(message = "사이즈 핏은 필수입니다")
    private SizeFit sizeFit;

    @NotNull(message = "쿠셔닝은 필수입니다")
    private Cushion cushioning;

    @NotNull(message = "안정성은 필수입니다")
    private Stability stability;

    @Size(max = 5, message = "이미지는 최대 5개까지 업로드 가능합니다")
    private List<String> imageUrls;
}