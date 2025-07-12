package com.cMall.feedShop.product.application.dto.response;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

class ProductListResponseTest {

    @Test
    void ProductListResponse_of() {
        // given
        Long productId = 1L;
        String name = "테스트 상품";
        BigDecimal price = new BigDecimal("50000");
        BigDecimal discountPrice = new BigDecimal("45000");
        Long storeId = 1L;
        String storeName = "테스트 스토어";
        Integer wishNumber = 10;
        String mainImageUrl = "http://test.jpg";

        // when
        ProductListResponse response = ProductListResponse.of(
                productId, name, price, discountPrice,
                storeId, storeName, wishNumber, mainImageUrl
        );

        // then
        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("테스트 상품");
        assertThat(response.getPrice()).isEqualTo(new BigDecimal("50000"));
        assertThat(response.getDiscountPrice()).isEqualTo(new BigDecimal("45000"));
        assertThat(response.getWishNumber()).isEqualTo(10);
    }

    @Test
    void ProductListResponse_of_wishNumber_null() {
        // when
        ProductListResponse response = ProductListResponse.of(
                1L, "상품", new BigDecimal("50000"), new BigDecimal("45000"),
                1L, "스토어", null, "http://test.jpg"
        );

        // then
        assertThat(response.getWishNumber()).isEqualTo(0);
    }
}