package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.product.application.dto.response.ProductCreateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductCreateResponse 테스트")
class ProductCreateResponseTest {

    @Test
    @DisplayName("productId가 주어졌을때_of 메서드 호출하면_ProductCreateResponse 객체가 생성된다")
    void givenProductId_whenCallOfMethod_thenCreateProductCreateResponse() {
        // given
        Long productId = 1L;

        // when
        ProductCreateResponse result = ProductCreateResponse.of(productId);

        // then
        assertThat(result.getProductId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("null productId가 주어졌을때_of 메서드 호출하면_null productId를 가진 응답이 생성된다")
    void givenNullProductId_whenCallOfMethod_thenCreateResponseWithNullId() {
        // given
        Long productId = null;

        // when
        ProductCreateResponse result = ProductCreateResponse.of(productId);

        // then
        assertThat(result.getProductId()).isNull();
    }

    @Test
    @DisplayName("큰 productId가 주어졌을때_of 메서드 호출하면_정확한 Long 값이 반환된다")
    void givenLargeProductId_whenCallOfMethod_thenReturnCorrectLongValue() {
        // given
        Long largeProductId = 9999999999L;

        // when
        ProductCreateResponse result = ProductCreateResponse.of(largeProductId);

        // then
        assertThat(result.getProductId()).isEqualTo(9999999999L);
    }
}