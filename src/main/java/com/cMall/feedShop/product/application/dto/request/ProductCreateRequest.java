package com.cMall.feedShop.product.application.dto.request;

import com.cMall.feedShop.product.domain.enums.DiscountType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {
    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max = 200, message = "상품명은 200자를 초과할 수 없습니다.")
    private String name;

    @NotNull(message = "가격은 필수입니다.")
    @DecimalMin(value = "0.01", message = "가격은 0보다 커야 합니다.")
    private BigDecimal price;

    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;

    private DiscountType discountType;

    private BigDecimal discountValue;

    private String description;

    @AssertTrue(message = "할인 타입이 설정된 경우 할인 값이 필요합니다.")
    public boolean isDiscountValid() {
        if (discountType == DiscountType.NONE) {
            return discountValue == null || discountValue.equals(BigDecimal.ZERO);
        }
        return discountValue != null && discountValue.compareTo(BigDecimal.ZERO) > 0;
    }
}