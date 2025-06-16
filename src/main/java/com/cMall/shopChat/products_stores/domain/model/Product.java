package products_stores.domain.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String shoesType;
    private int price;

    @ElementCollection
    private List<Integer> sizes;

    private int stock;
    private String description;

    // --- Getters ---
    public Long getId() {
        return id;
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

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

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
