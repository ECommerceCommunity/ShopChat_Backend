package com.cMall.feedShop.review.presentation;

import com.cMall.feedShop.review.application.ReviewService;
import com.cMall.feedShop.review.application.dto.response.ReviewDetailResponse;
import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.Stability;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(value = ReviewController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import({
        com.cMall.feedShop.common.aop.ResponseFormatAspect.class,
        com.cMall.feedShop.common.exception.GlobalExceptionHandler.class
}) // AOP와 ExceptionHandler 임포트
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=test_jwt_secret_key_for_testing_which_should_be_at_least_256_bit_long_string"
})
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("리뷰 상세 조회 API 테스트")
    void getReviewDetail_success() throws Exception {
        // given
        Long reviewId = 1L;
        ReviewDetailResponse response = ReviewDetailResponse.builder()
                .reviewId(reviewId)
                .productId(1L)
                .userId(1L)
                .userName("테스트사용자")
                .reviewTitle("테스트 리뷰")
                .rating(5)
                .content("정말 좋은 신발입니다")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(reviewService.getReviewDetail(reviewId)).thenReturn(response);

        // when & then - AOP가 적용되므로 ApiResponse 형태로 응답
        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("리뷰가 성공적으로 조회되었습니다."))
                .andExpect(jsonPath("$.data.reviewId").value(reviewId))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.sizeFit").value("PERFECT"))
                .andExpect(jsonPath("$.data.cushioning").value("VERY_SOFT"))
                .andExpect(jsonPath("$.data.stability").value("VERY_STABLE"))
                .andDo(print());

        verify(reviewService, times(1)).getReviewDetail(reviewId);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 조회 시 예외 처리")
    void getReviewDetail_notFound() throws Exception {
        // given
        Long reviewId = 999L;
        when(reviewService.getReviewDetail(reviewId))
                .thenThrow(new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("리뷰를 찾을 수 없습니다."))
                .andDo(print());

        verify(reviewService, times(1)).getReviewDetail(reviewId);
    }

    @Test
    @DisplayName("잘못된 reviewId 형식으로 요청 시 400 에러")
    void getReviewDetail_invalidId() throws Exception {
        // when & then
        mockMvc.perform(get("/api/reviews/{reviewId}", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("잘못된 형식의 파라미터입니다."))
                .andDo(print());

        verify(reviewService, never()).getReviewDetail(any());
    }

    @Test
    @DisplayName("음수 reviewId로 요청 시 validation 에러")
    void getReviewDetail_negativeId() throws Exception {
        // when & then
        mockMvc.perform(get("/api/reviews/{reviewId}", -1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("리뷰 ID는 양수여야 합니다"))
                .andDo(print());

        verify(reviewService, never()).getReviewDetail(any());
    }
}