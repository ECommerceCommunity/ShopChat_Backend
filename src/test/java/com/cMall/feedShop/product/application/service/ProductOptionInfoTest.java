package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.product.application.dto.ProductOptionInfo;
import com.cMall.feedShop.product.domain.enums.*;
import com.cMall.feedShop.product.domain.model.ProductOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductOptionInfo 테스트")
class ProductOptionInfoTest {

    @Mock
    private ProductOption productOption;

    @Test
    @DisplayName("ProductOption 엔티티가 주어졌을때_from 메서드 호출하면_ProductOptionInfo로 변환된다")
    void givenProductOption_whenCallFromMethod_thenReturnProductOptionInfo() {
        // given
        given(productOption.getOptionId()).willReturn(1L);
        given(productOption.getGender()).willReturn(Gender.UNISEX);
        given(productOption.getSize()).willReturn(Size.SIZE_250);
        given(productOption.getColor()).willReturn(Color.WHITE);
        given(productOption.getStock()).willReturn(100);

        // when
        ProductOptionInfo result = ProductOptionInfo.from(productOption);

        // then
        assertThat(result.getOptionId()).isEqualTo(1L);
        assertThat(result.getGender()).isEqualTo(Gender.UNISEX);
        assertThat(result.getSize()).isEqualTo(Size.SIZE_250);
        assertThat(result.getColor()).isEqualTo(Color.WHITE);
        assertThat(result.getStock()).isEqualTo(100);
    }

    @Test
    @DisplayName("ProductOption 리스트가 주어졌을때_fromList 메서드 호출하면_ProductOptionInfo 리스트로 변환된다")
    void givenProductOptionList_whenCallFromListMethod_thenReturnProductOptionInfoList() {
        // given
        List<ProductOption> options = List.of(productOption);
        given(productOption.getOptionId()).willReturn(1L);
        given(productOption.getGender()).willReturn(Gender.UNISEX);
        given(productOption.getSize()).willReturn(Size.SIZE_250);
        given(productOption.getColor()).willReturn(Color.WHITE);
        given(productOption.getStock()).willReturn(100);

        // when
        List<ProductOptionInfo> result = ProductOptionInfo.fromList(options);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOptionId()).isEqualTo(1L);
        assertThat(result.get(0).getGender()).isEqualTo(Gender.UNISEX);
        assertThat(result.get(0).getSize()).isEqualTo(Size.SIZE_250);
        assertThat(result.get(0).getColor()).isEqualTo(Color.WHITE);
        assertThat(result.get(0).getStock()).isEqualTo(100);
    }

    @Test
    @DisplayName("재고가 0인 ProductOption이 주어졌을때_from 메서드 호출하면_재고 0으로 변환된다")
    void givenProductOptionWithZeroStock_whenCallFromMethod_thenReturnWithZeroStock() {
        // given
        given(productOption.getOptionId()).willReturn(1L);
        given(productOption.getGender()).willReturn(Gender.UNISEX);
        given(productOption.getSize()).willReturn(Size.SIZE_250);
        given(productOption.getColor()).willReturn(Color.WHITE);
        given(productOption.getStock()).willReturn(0);

        // when
        ProductOptionInfo result = ProductOptionInfo.from(productOption);

        // then
        assertThat(result.getStock()).isZero();
    }
}