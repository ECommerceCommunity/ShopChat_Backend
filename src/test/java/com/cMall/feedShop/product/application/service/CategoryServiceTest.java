package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.product.application.dto.response.CategoryResponse;
import com.cMall.feedShop.product.domain.enums.CategoryType;
import com.cMall.feedShop.product.domain.model.Category;
import com.cMall.feedShop.product.domain.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService 테스트")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("카테고리가 존재할때_getAllCategories 호출하면_모든 카테고리 목록이 반환된다")
    void givenCategoriesExist_whenGetAllCategories_thenReturnAllCategories() {
        // given
        Category sneakers = new Category(CategoryType.SNEAKERS, "운동화");
        Category dress = new Category(CategoryType.RUNNING, "드레스");
        ReflectionTestUtils.setField(sneakers, "categoryId", 1L);
        ReflectionTestUtils.setField(dress, "categoryId", 2L);

        given(categoryRepository.findAll()).willReturn(List.of(sneakers, dress));

        // when
        List<CategoryResponse> result = categoryService.getAllCategories();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("운동화");
        assertThat(result.get(0).getType()).isEqualTo(CategoryType.SNEAKERS);
        assertThat(result.get(1).getName()).isEqualTo("드레스");
        assertThat(result.get(1).getType()).isEqualTo(CategoryType.RUNNING);
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("카테고리가 존재하지않을때_getAllCategories 호출하면_빈 목록이 반환된다")
    void givenNoCategoriesExist_whenGetAllCategories_thenReturnEmptyList() {
        // given
        given(categoryRepository.findAll()).willReturn(List.of());

        // when
        List<CategoryResponse> result = categoryService.getAllCategories();

        // then
        assertThat(result).isEmpty();
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("단일 카테고리가 존재할때_getAllCategories 호출하면_단일 카테고리가 반환된다")
    void givenSingleCategoryExists_whenGetAllCategories_thenReturnSingleCategory() {
        // given
        Category sneakers = new Category(CategoryType.SNEAKERS, "운동화");
        ReflectionTestUtils.setField(sneakers, "categoryId", 1L);
        given(categoryRepository.findAll()).willReturn(List.of(sneakers));

        // when
        List<CategoryResponse> result = categoryService.getAllCategories();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("운동화");
        assertThat(result.get(0).getType()).isEqualTo(CategoryType.SNEAKERS);
    }
}