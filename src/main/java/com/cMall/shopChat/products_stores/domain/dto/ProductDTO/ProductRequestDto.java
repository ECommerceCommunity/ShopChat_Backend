package products_stores.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ProductRequestDto {

    @NotBlank(message = "상품 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "카테고리는 필수입니다.")
    private String shoesType;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    @NotNull(message = "사이즈 목록은 필수입니다.")
    private List<Integer> sizes; // ← 수정됨

    private int stock;

    private String description;

    // --- Getters ---
    public String getName() {
        return name;
    }

    public String getShoesType() {
        return shoesType;
    }

    public int getPrice() {
        return price;
    }

    public List<Integer> getSizes() {
        return sizes;
    }

    public int getStock() {
        return stock;
    }

    public String getDescription() {
        return description;
    }

    // --- Setters ---
    public void setName(String name) {
        this.name = name;
    }

    public void setShoesType(String shoesType) {
        this.shoesType = shoesType;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setSizes(List<Integer> sizes) {
        this.sizes = sizes;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
