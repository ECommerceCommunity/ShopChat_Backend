package com.cMall.feedShop.stores.domain;

import com.cMall.feedShop.products.domain.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stores")
@Getter // ✅ 이게 꼭 있어야 합니다!
@NoArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String logo; // 이 필드가 있어야 합니다

    @Column(name = "numbers_likes")
    private Integer numbersLikes = 0;

    @Column(name = "store_name", length = 100, nullable = false)
    private String name;

    @OneToMany(mappedBy = "store")
    private List<Product> products = new ArrayList<>();

    public Store(String description, String logo, String name) {
        this.description = description;
        this.logo = logo;
        this.name = name;
        this.numbersLikes = 0;
    }

    public void increaseLikes() {
        this.numbersLikes++;
    }
}
