package com.cMall.feedShop.product.domain.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CategoryTypeTest {

    @Test
    void CategoryType_values() {
        // when & then
        assertThat(CategoryType.values()).hasSize(5);
        assertThat(CategoryType.SNEAKERS).isNotNull();
        assertThat(CategoryType.DRESS).isNotNull();
        assertThat(CategoryType.BOOTS).isNotNull();
        assertThat(CategoryType.SANDALS).isNotNull();
        assertThat(CategoryType.CASUAL).isNotNull();
    }
}