package products_stores.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class DiscountRequestDto {

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    @NotBlank(message = "할인 유형은 필수입니다.")
    private String discountType;

    @Min(value = 0, message = "할인 금액은 0 이상이어야 합니다.")
    private int amount;

    @NotNull(message = "할인 시작일은 필수입니다.")
    private LocalDate startDate;

    private LocalDate endDate;

    // --- Getters ---
    public Long getProductId() {
        return productId;
    }

    public String getDiscountType() {
        return discountType;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    // --- Setters ---
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
