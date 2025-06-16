package products_stores.domain.dto;

import products_stores.domain.model.Discount;

import java.time.LocalDate;

public class DiscountResponseDto {

    private Long id;
    private Long productId;
    private String discountType;
    private int amount;
    private LocalDate startDate;
    private LocalDate endDate;

    public DiscountResponseDto(Discount discount) {
        this.id = discount.getId();
        this.productId = discount.getProductId();
        this.discountType = discount.getDiscountType();
        this.amount = discount.getAmount();
        this.startDate = discount.getStartDate();
        this.endDate = discount.getEndDate();
    }

    // --- Getters ---
    public Long getId() {
        return id;
    }

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
}
