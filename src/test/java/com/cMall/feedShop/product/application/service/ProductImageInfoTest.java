package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.product.application.dto.ProductImageInfo;
import com.cMall.feedShop.product.domain.enums.ImageType;
import com.cMall.feedShop.product.domain.model.ProductImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductImageInfo 테스트")
class ProductImageInfoTest {

    @Mock
    private ProductImage productImage;

    @Test
    @DisplayName("ProductImage 엔티티가 주어졌을때_from 메서드 호출하면_ProductImageInfo로 변환된다")
    void givenProductImage_whenCallFromMethod_thenReturnProductImageInfo() {
        // given
        given(productImage.getImageId()).willReturn(1L);
        given(productImage.getUrl()).willReturn("http://test.jpg");
        given(productImage.getType()).willReturn(ImageType.MAIN);

        // when
        ProductImageInfo result = ProductImageInfo.from(productImage);

        // then
        assertThat(result.getImageId()).isEqualTo(1L);
        assertThat(result.getUrl()).isEqualTo("http://test.jpg");
        assertThat(result.getType()).isEqualTo(ImageType.MAIN);
    }

    @Test
    @DisplayName("ProductImage 리스트가 주어졌을때_fromList 메서드 호출하면_ProductImageInfo 리스트로 변환된다")
    void givenProductImageList_whenCallFromListMethod_thenReturnProductImageInfoList() {
        // given
        List<ProductImage> images = List.of(productImage);
        given(productImage.getImageId()).willReturn(1L);
        given(productImage.getUrl()).willReturn("http://test.jpg");
        given(productImage.getType()).willReturn(ImageType.MAIN);

        // when
        List<ProductImageInfo> result = ProductImageInfo.fromList(images);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getImageId()).isEqualTo(1L);
        assertThat(result.get(0).getUrl()).isEqualTo("http://test.jpg");
        assertThat(result.get(0).getType()).isEqualTo(ImageType.MAIN);
    }

    @Test
    @DisplayName("빈 ProductImage 리스트가 주어졌을때_fromList 메서드 호출하면_빈 ProductImageInfo 리스트가 반환된다")
    void givenEmptyProductImageList_whenCallFromListMethod_thenReturnEmptyList() {
        // given
        List<ProductImage> emptyImages = List.of();

        // when
        List<ProductImageInfo> result = ProductImageInfo.fromList(emptyImages);

        // then
        assertThat(result).isEmpty();
    }
}