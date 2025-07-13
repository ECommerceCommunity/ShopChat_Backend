package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.product.domain.enums.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Color Enum 테스트")
class ColorTest {
    @Test
    @DisplayName("Color enum이 정의되었을때_values 호출하면_모든 색상이 반환된다")
    void givenColorEnum_whenCallValues_thenReturnAllColors() {
        // when
        Color[] result = Color.values();

        // then
        assertThat(result).hasSize(44);
        assertThat(result).contains(
                Color.WHITE, Color.BLACK, Color.RED, Color.BLUE,
                Color.GREEN, Color.YELLOW, Color.PURPLE, Color.BROWN
        );
    }

    @Test
    @DisplayName("기본 색상들이 정의되었을때_객체 생성하면_null이 아니다")
    void givenBasicColors_whenCreateObjects_thenNotNull() {
        // when & then
        assertThat(Color.WHITE).isNotNull();
        assertThat(Color.BLACK).isNotNull();
        assertThat(Color.RED).isNotNull();
        assertThat(Color.BLUE).isNotNull();
        assertThat(Color.GREEN).isNotNull();
    }

    @Test
    @DisplayName("Color가 주어졌을때_name 호출하면_정확한 이름이 반환된다")
    void givenColor_whenCallName_thenReturnCorrectName() {
        // when & then
        assertThat(Color.WHITE.name()).isEqualTo("WHITE");
        assertThat(Color.BLACK.name()).isEqualTo("BLACK");
        assertThat(Color.RED.name()).isEqualTo("RED");
        assertThat(Color.BLUE.name()).isEqualTo("BLUE");
    }

    @Test
    @DisplayName("첫번째와 마지막 색상이_올바르게 정의되어있다")
    void givenFirstAndLastColors_whenCheck_thenCorrect() {
        // given
        Color[] colors = Color.values();

        // when & then
        assertThat(colors[0]).isEqualTo(Color.WHITE);
        assertThat(colors[colors.length - 1]).isEqualTo(Color.KHAKI_BEIGE);
        assertThat(colors).hasSize(44);
    }
}