package com.cMall.feedShop.product.application.util;

import com.cMall.feedShop.product.domain.enums.DiscountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("할인 계산기 테스트")
public class DiscountCalculatorTest {
    private DiscountCalculator discountCalculator;

    @BeforeEach
    void setUp() {
        discountCalculator = new DiscountCalculator();
    }

    @Test
    @DisplayName("할인 없음")
    void calculateDiscountPrice_NoDiscount() {
        // Given
        BigDecimal originalPrice = new BigDecimal("50000");

        // When
        BigDecimal result = discountCalculator.calculateDiscountPrice(originalPrice, DiscountType.NONE, null);

        // Then
        assertThat(result).isEqualTo(originalPrice);
    }

    @Test
    @DisplayName("고정 할인")
    void calculateDiscountPrice_FixedDiscount() {
        // Given
        BigDecimal originalPrice = new BigDecimal("50000");
        BigDecimal discountValue = new BigDecimal("5000");

        // When
        BigDecimal result = discountCalculator.calculateDiscountPrice(originalPrice, DiscountType.FIXED_DISCOUNT, discountValue);

        // Then
        assertThat(result).isEqualTo(new BigDecimal("45000"));
    }

    @Test
    @DisplayName("비율 할인")
    void calculateDiscountPrice_RateDiscount() {
        // Given
        BigDecimal originalPrice = new BigDecimal("50000");
        BigDecimal discountValue = new BigDecimal("10");

        // When
        BigDecimal result = discountCalculator.calculateDiscountPrice(originalPrice, DiscountType.RATE_DISCOUNT, discountValue);

        // Then
        assertThat(result).isEqualTo(new BigDecimal("45000"));
    }
}
