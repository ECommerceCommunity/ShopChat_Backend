package com.cMall.feedShop.product.application.dto;

import com.cMall.feedShop.product.domain.enums.*;
import com.cMall.feedShop.product.domain.model.ProductOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductOptionInfoTest {

    @Mock
    private ProductOption productOption;

    @Test
    void ProductOptionInfo_from() {
        // given
        given(productOption.getOptionId()).willReturn(1L);
        given(productOption.getGender()).willReturn(Gender.UNISEX);
        given(productOption.getSize()).willReturn(Size.SIZE_250);
        given(productOption.getColor()).willReturn(Color.BLACK);
        given(productOption.getStock()).willReturn(100);

        // when
        ProductOptionInfo info = ProductOptionInfo.from(productOption);

        // then
        assertThat(info.getOptionId()).isEqualTo(1L);
        assertThat(info.getGender()).isEqualTo(Gender.UNISEX);
        assertThat(info.getSize()).isEqualTo(Size.SIZE_250);
        assertThat(info.getColor()).isEqualTo(Color.BLACK);
        assertThat(info.getStock()).isEqualTo(100);
    }

    @Test
    void ProductOptionInfo_fromList() {
        // given
        List<ProductOption> options = List.of(productOption);
        given(productOption.getOptionId()).willReturn(1L);
        given(productOption.getGender()).willReturn(Gender.UNISEX);
        given(productOption.getSize()).willReturn(Size.SIZE_250);
        given(productOption.getColor()).willReturn(Color.BLACK);
        given(productOption.getStock()).willReturn(100);

        // when
        List<ProductOptionInfo> infoList = ProductOptionInfo.fromList(options);

        // then
        assertThat(infoList).hasSize(1);
        assertThat(infoList.get(0).getOptionId()).isEqualTo(1L);
    }
}