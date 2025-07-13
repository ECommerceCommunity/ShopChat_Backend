package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.product.domain.enums.CategoryType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CategoryType Enum 테스트")
class CategoryTypeTest {

    @Test
    @DisplayName("CategoryType enum이 정의되었을때_values 호출하면_모든 카테고리 타입이 반환된다")
    void givenCategoryTypeEnum_whenCallValues_thenReturnAllCategoryTypes() {
        // when
        CategoryType[] result = CategoryType.values();

        // then - 실제 존재하는 7개로 수정
        assertThat(result).hasSize(7);
        assertThat(result).contains(
                CategoryType.SNEAKERS,
                CategoryType.RUNNING,    // 추가
                CategoryType.BOOTS,
                CategoryType.SANDALS,
                CategoryType.CONVERSE,   // 추가
                CategoryType.SPORTS,     // 추가
                CategoryType.DRESS
                // CategoryType.CASUAL 제거 (실제로 존재하지 않음)
        );
    }

    @Test
    @DisplayName("CategoryType이 주어졌을때_name 호출하면_정확한 이름이 반환된다")
    void givenCategoryType_whenCallName_thenReturnCorrectName() {
        // when & then - 실제 존재하는 값들로 수정
        assertThat(CategoryType.SNEAKERS.name()).isEqualTo("SNEAKERS");
        assertThat(CategoryType.RUNNING.name()).isEqualTo("RUNNING");   // 수정
        assertThat(CategoryType.BOOTS.name()).isEqualTo("BOOTS");
        assertThat(CategoryType.SANDALS.name()).isEqualTo("SANDALS");
        assertThat(CategoryType.CONVERSE.name()).isEqualTo("CONVERSE"); // 수정
        assertThat(CategoryType.SPORTS.name()).isEqualTo("SPORTS");     // 수정
        assertThat(CategoryType.DRESS.name()).isEqualTo("DRESS");
    }

    @Test
    @DisplayName("CategoryType이 주어졌을때_toString 호출하면_정확한 문자열이 반환된다")
    void givenCategoryType_whenCallToString_thenReturnCorrectString() {
        // when & then - 실제 순서대로 수정
        assertThat(CategoryType.SNEAKERS.toString()).hasToString("SNEAKERS");
        assertThat(CategoryType.RUNNING.toString()).hasToString("RUNNING");   // 수정
        assertThat(CategoryType.BOOTS.toString()).hasToString("BOOTS");
        assertThat(CategoryType.SANDALS.toString()).hasToString("SANDALS");
        assertThat(CategoryType.CONVERSE.toString()).hasToString("CONVERSE"); // 수정
        assertThat(CategoryType.SPORTS.toString()).hasToString("SPORTS");     // 수정
        assertThat(CategoryType.DRESS.toString()).hasToString("DRESS");
    }

    @Test
    @DisplayName("동일한 CategoryType이 주어졌을때_equals 비교하면_true가 반환된다")
    void givenSameCategoryType_whenCompareEquals_thenReturnTrue() {
        // when & then
        assertThat(CategoryType.SNEAKERS).isEqualTo(CategoryType.SNEAKERS);
        assertThat(CategoryType.RUNNING).isEqualTo(CategoryType.RUNNING); // 수정
    }

    @Test
    @DisplayName("다른 CategoryType이 주어졌을때_equals 비교하면_false가 반환된다")
    void givenDifferentCategoryType_whenCompareEquals_thenReturnFalse() {
        // when & then
        assertThat(CategoryType.SNEAKERS).isNotEqualTo(CategoryType.RUNNING); // 수정
        assertThat(CategoryType.BOOTS).isNotEqualTo(CategoryType.SPORTS);     // 수정
    }
}