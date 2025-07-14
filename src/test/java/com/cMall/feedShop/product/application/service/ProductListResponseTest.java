package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.product.application.dto.response.ProductListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductListResponse 테스트")
class ProductListResponseTest {

    @Test
    @DisplayName("모든 필드가 유효할때_of 메서드 호출하면_ProductListResponse 객체가 생성된다")
    void givenValidFields_whenCallOfMethod_thenCreateProductListResponse() {
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
        ProductListResponse result = ProductListResponse.of(
                productId, name, price, discountPrice,
                storeId, storeName, wishNumber, mainImageUrl
        );

        // then
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("테스트 상품");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("50000"));
        assertThat(result.getDiscountPrice()).isEqualTo(new BigDecimal("45000"));
        assertThat(result.getStoreId()).isEqualTo(1L);
        assertThat(result.getStoreName()).isEqualTo("테스트 스토어");
        assertThat(result.getWishNumber()).isEqualTo(10);
        assertThat(result.getMainImageUrl()).isEqualTo("http://test.jpg");
    }

    @Test
    @DisplayName("wishNumber가 null일때_of 메서드 호출하면_wishNumber가 0으로 설정된다")
    void givenNullWishNumber_whenCallOfMethod_thenWishNumberIsZero() {
        // given
        Long productId = 1L;
        String name = "상품";
        BigDecimal price = new BigDecimal("50000");
        BigDecimal discountPrice = new BigDecimal("45000");
        Long storeId = 1L;
        String storeName = "스토어";
        Integer wishNumber = null;
        String mainImageUrl = "http://test.jpg";

        // when
        ProductListResponse result = ProductListResponse.of(
                productId, name, price, discountPrice,
                storeId, storeName, wishNumber, mainImageUrl
        );

        // then
        assertThat(result.getWishNumber()).isEqualTo(0);
    }

    @Test
    @DisplayName("할인가격이 원가보다 클때_of 메서드 호출하면_정확한 값으로 설정된다")
    void givenDiscountPriceHigherThanPrice_whenCallOfMethod_thenSetCorrectValues() {
        // given
        BigDecimal price = new BigDecimal("30000");
        BigDecimal discountPrice = new BigDecimal("35000");

        // when
        ProductListResponse result = ProductListResponse.of(
                1L, "상품", price, discountPrice,
                1L, "스토어", 5, "http://test.jpg"
        );

        // then
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("30000"));
        assertThat(result.getDiscountPrice()).isEqualTo(new BigDecimal("35000"));
    }

    @Test
    @DisplayName("mainImageUrl이 null일때_of 메서드 호출하면_null로 설정된다")
    void givenNullMainImageUrl_whenCallOfMethod_thenMainImageUrlIsNull() {
        // given
        String mainImageUrl = null;

        // when
        ProductListResponse result = ProductListResponse.of(
                1L, "상품", new BigDecimal("50000"), new BigDecimal("45000"),
                1L, "스토어", 0, mainImageUrl
        );

        // then
        assertThat(result.getMainImageUrl()).isNull();
    }
}