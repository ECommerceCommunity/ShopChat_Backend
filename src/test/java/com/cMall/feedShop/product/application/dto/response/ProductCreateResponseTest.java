package com.cMall.feedShop.product.application.dto.response;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ProductCreateResponseTest {

    @Test
    void ProductCreateResponse_of() {
        // given
        Long productId = 1L;

        // when
        ProductCreateResponse response = ProductCreateResponse.of(productId);

        // then
        assertThat(response.getProductId()).isEqualTo(1L);
    }
}