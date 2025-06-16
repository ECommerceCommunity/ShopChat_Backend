package products_stores.domain.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId; // 연결된 상품 ID
    private String discountType;
    private int amount;
    private LocalDate startDate;
    private LocalDate endDate;

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

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

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
