package products_stores.domain.dto;

import products_stores.domain.model.Product;

import java.util.List;

public class ProductResponseDto {

    private Long shoe_id; // 또는 Long id; ← 명세에 맞추세요
    private String name;
    private String shoesType;
    private int price;
    private List<Integer> sizes;
    private int stock;
    private String description;

    public ProductResponseDto(Product product) {
        this.shoe_id = product.getId(); // 또는 this.id = ...
        this.name = product.getName();
        this.shoesType = product.getShoesType();
        this.price = product.getPrice();
        this.sizes = product.getSizes();
        this.stock = product.getStock();
        this.description = product.getDescription();
    }

    // --- Getters ---
    public Long getShoe_id() {
        return shoe_id;
    }

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
}
