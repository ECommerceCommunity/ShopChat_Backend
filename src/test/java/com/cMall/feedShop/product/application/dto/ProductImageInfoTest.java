package com.cMall.feedShop.product.application.dto;

import com.cMall.feedShop.product.domain.enums.ImageType;
import com.cMall.feedShop.product.domain.model.ProductImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductImageInfoTest {

    @Mock
    private ProductImage productImage;

    @Test
    void ProductImageInfo_from() {
        // given
        given(productImage.getImageId()).willReturn(1L);
        given(productImage.getUrl()).willReturn("http://test.jpg");
        given(productImage.getType()).willReturn(ImageType.MAIN);

        // when
        ProductImageInfo info = ProductImageInfo.from(productImage);

        // then
        assertThat(info.getImageId()).isEqualTo(1L);
        assertThat(info.getUrl()).isEqualTo("http://test.jpg");
        assertThat(info.getType()).isEqualTo(ImageType.MAIN);
    }

    @Test
    void ProductImageInfo_fromList() {
        // given
        List<ProductImage> images = List.of(productImage);
        given(productImage.getImageId()).willReturn(1L);
        given(productImage.getUrl()).willReturn("http://test.jpg");
        given(productImage.getType()).willReturn(ImageType.MAIN);

        // when
        List<ProductImageInfo> infoList = ProductImageInfo.fromList(images);

        // then
        assertThat(infoList).hasSize(1);
        assertThat(infoList.get(0).getImageId()).isEqualTo(1L);
    }
}