package com.cMall.feedShop.product.domain.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ColorTest {

    @Test
    void Color_values() {
        // when & then
        assertThat(Color.values()).hasSize(12);
        assertThat(Color.BLACK).isNotNull();
        assertThat(Color.WHITE).isNotNull();
        assertThat(Color.RED).isNotNull();
    }
}