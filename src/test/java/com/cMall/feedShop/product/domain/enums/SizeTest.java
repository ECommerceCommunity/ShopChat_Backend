package com.cMall.feedShop.product.domain.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SizeTest {

    @Test
    void Size_getValue() {
        // when & then
        assertThat(Size.SIZE_250.getValue()).isEqualTo("250");
        assertThat(Size.SIZE_280.getValue()).isEqualTo("280");
    }

    @Test
    void Size_fromValue_success() {
        // when
        Size size = Size.fromValue("250");

        // then
        assertThat(size).isEqualTo(Size.SIZE_250);
    }

    @Test
    void Size_fromValue_fail() {
        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                Size.fromValue("999"));
    }

    @Test
    void Size_toString() {
        // when & then
        assertThat(Size.SIZE_250.toString()).isEqualTo("250");
    }
}