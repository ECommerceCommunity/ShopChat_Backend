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

@ExtendWith(MockitoExtension.class)
@DisplayName("카테고리 서비스 테스트")
public class CategoryServiceTest {
    @Mock private CategoryRepository categoryRepository;
    @InjectMocks private CategoryService categoryService;

    @Test
    @DisplayName("카테고리 목록 조회 성공")
    void getAllCategories_Success() {
        // Given
        Category sneakers = new Category(CategoryType.SNEAKERS, "운동화");
        ReflectionTestUtils.setField(sneakers, "categoryId", 1L);
        given(categoryRepository.findAll()).willReturn(List.of(sneakers));

        // When
        List<CategoryResponse> responses = categoryService.getAllCategories();

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("운동화");
    }
}
