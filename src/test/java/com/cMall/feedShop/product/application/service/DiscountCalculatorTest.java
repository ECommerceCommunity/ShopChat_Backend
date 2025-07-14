package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.product.application.util.DiscountCalculator;
import com.cMall.feedShop.product.domain.enums.DiscountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DiscountCalculator 테스트")
class DiscountCalculatorTest {

    private DiscountCalculator discountCalculator;

    @BeforeEach
    void setUp() {
        discountCalculator = new DiscountCalculator();
    }

    @Test
    @DisplayName("할인타입이 NONE일때_calculateDiscountPrice 호출하면_원가가 그대로 반환된다")
    void givenDiscountTypeNone_whenCalculateDiscountPrice_thenReturnOriginalPrice() {
        // given
        BigDecimal originalPrice = new BigDecimal("50000");

        // when
        BigDecimal result = discountCalculator.calculateDiscountPrice(
                originalPrice, DiscountType.NONE, null);

        // then
        assertThat(result).isEqualTo(originalPrice);
    }

    @Test
    @DisplayName("고정할인값이 주어졌을때_calculateDiscountPrice 호출하면_할인된 가격이 반환된다")
    void givenFixedDiscountValue_whenCalculateDiscountPrice_thenReturnDiscountedPrice() {
        // given
        BigDecimal originalPrice = new BigDecimal("50000");
        BigDecimal discountValue = new BigDecimal("5000");

        // when
        BigDecimal result = discountCalculator.calculateDiscountPrice(
                originalPrice, DiscountType.FIXED_DISCOUNT, discountValue);

        // then
        assertThat(result).isEqualTo(new BigDecimal("45000"));
    }

    @Test
    @DisplayName("비율할인값이 주어졌을때_calculateDiscountPrice 호출하면_할인된 가격이 반환된다")
    void givenRateDiscountValue_whenCalculateDiscountPrice_thenReturnDiscountedPrice() {
        // given
        BigDecimal originalPrice = new BigDecimal("50000");
        BigDecimal discountValue = new BigDecimal("10"); // 10%

        // when
        BigDecimal result = discountCalculator.calculateDiscountPrice(
                originalPrice, DiscountType.RATE_DISCOUNT, discountValue);

        // then
        assertThat(result).isEqualTo(new BigDecimal("45000"));
    }

    @Test
    @DisplayName("고정할인값이 원가보다 클때_calculateDiscountPrice 호출하면_0원이 반환된다")
    void givenFixedDiscountGreaterThanPrice_whenCalculateDiscountPrice_thenReturnZero() {
        // given
        BigDecimal originalPrice = new BigDecimal("30000");
        BigDecimal discountValue = new BigDecimal("50000");

        // when
        BigDecimal result = discountCalculator.calculateDiscountPrice(
                originalPrice, DiscountType.FIXED_DISCOUNT, discountValue);

        // then
        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("비율할인이 100퍼센트일때_calculateDiscountPrice 호출하면_0원이 반환된다")
    void givenRateDiscount100Percent_whenCalculateDiscountPrice_thenReturnZero() {
        // given
        BigDecimal originalPrice = new BigDecimal("50000");
        BigDecimal discountValue = new BigDecimal("100"); // 100%

        // when
        BigDecimal result = discountCalculator.calculateDiscountPrice(
                originalPrice, DiscountType.RATE_DISCOUNT, discountValue);

        // then
        assertThat(result).isEqualTo(new BigDecimal("0"));
    }

    @Test
    @DisplayName("원가가 null일때_calculateDiscountPrice 호출하면_null이 반환된다")
    void givenNullOriginalPrice_whenCalculateDiscountPrice_thenReturnNull() {
        // given
        BigDecimal originalPrice = null;
        BigDecimal discountValue = new BigDecimal("5000");

        // when
        BigDecimal result = discountCalculator.calculateDiscountPrice(
                originalPrice, DiscountType.FIXED_DISCOUNT, discountValue);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("할인값이 null일때_calculateDiscountPrice 호출하면_원가가 반환된다")
    void givenNullDiscountValue_whenCalculateDiscountPrice_thenReturnOriginalPrice() {
        // given
        BigDecimal originalPrice = new BigDecimal("50000");
        BigDecimal discountValue = null;

        // when
        BigDecimal result = discountCalculator.calculateDiscountPrice(
                originalPrice, DiscountType.FIXED_DISCOUNT, discountValue);

        // then
        assertThat(result).isEqualTo(originalPrice);
    }

    @Test
    @DisplayName("할인값이 0일때_calculateDiscountPrice 호출하면_원가가 반환된다")
    void givenZeroDiscountValue_whenCalculateDiscountPrice_thenReturnOriginalPrice() {
        // given
        BigDecimal originalPrice = new BigDecimal("50000");
        BigDecimal discountValue = BigDecimal.ZERO;

        // when
        BigDecimal result = discountCalculator.calculateDiscountPrice(
                originalPrice, DiscountType.FIXED_DISCOUNT, discountValue);

        // then
        assertThat(result).isEqualTo(originalPrice);
    }

    @Test
    @DisplayName("음수 할인값이 주어졌을때_calculateDiscountPrice 호출하면_원가가 반환된다")
    void givenNegativeDiscountValue_whenCalculateDiscountPrice_thenReturnOriginalPrice() {
        // given
        BigDecimal originalPrice = new BigDecimal("50000");
        BigDecimal discountValue = new BigDecimal("-1000");

        // when
        BigDecimal result = discountCalculator.calculateDiscountPrice(
                originalPrice, DiscountType.FIXED_DISCOUNT, discountValue);

        // then
        assertThat(result).isEqualTo(originalPrice);
    }

    @Test
    @DisplayName("소수점이 포함된 비율할인이 주어졌을때_calculateDiscountPrice 호출하면_반올림된 가격이 반환된다")
    void givenDecimalRateDiscount_whenCalculateDiscountPrice_thenReturnRoundedPrice() {
        // given
        BigDecimal originalPrice = new BigDecimal("10000");
        BigDecimal discountValue = new BigDecimal("33.33"); // 33.33%

        // when
        BigDecimal result = discountCalculator.calculateDiscountPrice(
                originalPrice, DiscountType.RATE_DISCOUNT, discountValue);

        // then
        assertThat(result).isEqualTo(new BigDecimal("6667")); // 반올림 결과
    }
}