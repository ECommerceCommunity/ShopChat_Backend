package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.product.domain.enums.Size;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Size Enum 테스트")
class SizeTest {

    @Test
    @DisplayName("Size enum이 정의되었을때_getValue 호출하면_정확한 사이즈 값이 반환된다")
    void givenSizeEnum_whenGetValue_thenReturnCorrectSizeValue() {
        // when & then
        Assertions.assertThat(Size.SIZE_250.getValue()).isEqualTo("250");
        assertThat(Size.SIZE_280.getValue()).isEqualTo("280");
        assertThat(Size.SIZE_230.getValue()).isEqualTo("230");
        assertThat(Size.SIZE_300.getValue()).isEqualTo("300");
    }

    @Test
    @DisplayName("유효한 사이즈 값이 주어졌을때_fromValue 호출하면_해당 Size enum이 반환된다")
    void givenValidSizeValue_whenFromValue_thenReturnCorrespondingSize() {
        // when
        Size result250 = Size.fromValue("250");
        Size result280 = Size.fromValue("280");

        // then
        assertThat(result250).isEqualTo(Size.SIZE_250);
        assertThat(result280).isEqualTo(Size.SIZE_280);
    }

    @Test
    @DisplayName("유효하지않은 사이즈 값이 주어졌을때_fromValue 호출하면_예외가 발생한다")
    void givenInvalidSizeValue_whenFromValue_thenThrowException() {
        // when & then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                Size.fromValue("999"));

        assertThat(thrown.getMessage()).contains("Invalid size: 999");
    }

    @Test
    @DisplayName("null 사이즈 값이 주어졌을때_fromValue 호출하면_예외가 발생한다")
    void givenNullSizeValue_whenFromValue_thenThrowException() {
        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                Size.fromValue(null));
    }

    @Test
    @DisplayName("빈 문자열 사이즈 값이 주어졌을때_fromValue 호출하면_예외가 발생한다")
    void givenEmptySizeValue_whenFromValue_thenThrowException() {
        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                Size.fromValue(""));
    }

    @Test
    @DisplayName("Size enum이 주어졌을때_toString 호출하면_사이즈 값이 반환된다")
    void givenSizeEnum_whenToString_thenReturnSizeValue() {
        // when & then
        assertThat(Size.SIZE_250.toString()).hasToString("250");
        assertThat(Size.SIZE_265.toString()).hasToString("265");
        assertThat(Size.SIZE_290.toString()).hasToString("290");
    }

    @Test
    @DisplayName("모든 Size enum이 정의되었을때_values 호출하면_15개 사이즈가 반환된다")
    void givenAllSizeEnums_whenValues_thenReturn15Sizes() {
        // when
        Size[] sizes = Size.values();

        // then
        assertThat(sizes).hasSize(15);
        assertThat(sizes).contains(
                Size.SIZE_230, Size.SIZE_235, Size.SIZE_240, Size.SIZE_245, Size.SIZE_250,
                Size.SIZE_255, Size.SIZE_260, Size.SIZE_265, Size.SIZE_270, Size.SIZE_275,
                Size.SIZE_280, Size.SIZE_285, Size.SIZE_290, Size.SIZE_295, Size.SIZE_300
        );
    }

    @Test
    @DisplayName("최소 사이즈와 최대 사이즈가 정의되었을때_올바른 범위가 확인된다")
    void givenMinAndMaxSizes_whenCheckRange_thenCorrectRange() {
        // when & then
        assertThat(Size.SIZE_230.getValue()).isEqualTo("230");
        assertThat(Size.SIZE_300.getValue()).isEqualTo("300");
    }

    @Test
    @DisplayName("Size enum 순서가 주어졌을때_ordinal 값이 정확하다")
    void givenSizeEnumOrder_whenCheckOrdinal_thenCorrectOrder() {
        // when & then
        assertThat(Size.SIZE_230.ordinal()).isLessThan(Size.SIZE_235.ordinal());
        assertThat(Size.SIZE_235.ordinal()).isLessThan(Size.SIZE_240.ordinal());
        assertThat(Size.SIZE_295.ordinal()).isLessThan(Size.SIZE_300.ordinal());
    }

    @Test
    @DisplayName("5mm 단위 사이즈가 주어졌을때_모든 사이즈가 5의 배수 또는 0으로 끝난다")
    void givenSizesInFiveMillimeterUnits_whenCheckValues_thenAllEndWithFiveOrZero() {
        // when
        Size[] sizes = Size.values();

        // then
        for (Size size : sizes) {
            String value = size.getValue();
            String lastDigit = value.substring(value.length() - 1);
            assertThat(lastDigit).isIn("0", "5");
        }
    }

    @Test
    @DisplayName("동일한 Size가 주어졌을때_equals 비교하면_true가 반환된다")
    void givenSameSize_whenCompareEquals_thenReturnTrue() {
        // when & then
        assertThat(Size.SIZE_250).isEqualTo(Size.SIZE_250);
        assertThat(Size.SIZE_280).isEqualTo(Size.SIZE_280);
    }

    @Test
    @DisplayName("다른 Size가 주어졌을때_equals 비교하면_false가 반환된다")
    void givenDifferentSize_whenCompareEquals_thenReturnFalse() {
        // when & then
        assertThat(Size.SIZE_250).isNotEqualTo(Size.SIZE_255);
        assertThat(Size.SIZE_270).isNotEqualTo(Size.SIZE_275);
    }
}