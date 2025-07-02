package com.cMall.feedShop.products.domain;

import com.cMall.feedShop.products.domain.DiscountPrice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "product_discounts")
@Getter
@NoArgsConstructor
public class ProductDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private DiscountPrice discountPrice;

    @Setter
    @Column(name = "start_date")
    private LocalDate startDate;

    @Setter
    @Column(name = "end_date")
    private LocalDate endDate;
}
