package com.cMall.feedShop.review.presentation;

import com.cMall.feedShop.review.application.ReviewService;
import com.cMall.feedShop.review.application.dto.request.ReviewCreateRequest;
import com.cMall.feedShop.review.application.dto.response.ReviewCreateResponse;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(value = ReviewUserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import({
        com.cMall.feedShop.common.aop.ResponseFormatAspect.class,
        com.cMall.feedShop.common.exception.GlobalExceptionHandler.class
})
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=test_jwt_secret_key_for_testing_which_should_be_at_least_256_bit_long_string"
})
public class ReviewUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("리뷰 생성 API 테스트")
    void createReview_success() throws Exception {
        // given
        Long userId = 1L;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .userId(userId)
                .productId(1L)
                .reviewTitle("훌륭한 상품!")
                .rating(5)
                .content("정말 좋은 상품입니다. 추천해요!")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();

        ReviewCreateResponse response = ReviewCreateResponse.builder()
                .reviewId(1L)
                .productId(1L)
                .userId(userId)
                .reviewTitle("훌륭한 상품!")
                .rating(5)
                .content("정말 좋은 상품입니다. 추천해요!")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewService.createReview(any(ReviewCreateRequest.class))).thenReturn(response);

        // when & then - AOP가 적용되므로 ApiResponse 형태로 응답
        mockMvc.perform(post("/api/users/{userId}/reviews", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("리뷰가 성공적으로 등록되었습니다."))
                .andExpect(jsonPath("$.data.reviewId").value(1))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.sizeFit").value("PERFECT"))
                .andExpect(jsonPath("$.data.cushioning").value("VERY_SOFT"))
                .andExpect(jsonPath("$.data.stability").value("VERY_STABLE"))
                .andExpect(jsonPath("$.data.reviewTitle").value("훌륭한 상품!"))
                .andDo(print());

        verify(reviewService, times(1)).createReview(any(ReviewCreateRequest.class));
    }

    @Test
    @DisplayName("잘못된 JSON 형식으로 요청")
    void createReview_invalidJson() throws Exception {
        // given - 잘못된 JSON
        String invalidJson = "{ invalid json }";

        // when & then
        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());

        verify(reviewService, never()).createReview(any(ReviewCreateRequest.class));
    }

    @Test
    @DisplayName("경계값 테스트 - 최소 유효 평점")
    void createReview_minimumRating() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .userId(1L)
                .productId(1L)
                .reviewTitle("최소 평점 테스트")
                .rating(1) // 최소값
                .content("내용")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();

        ReviewCreateResponse response = ReviewCreateResponse.builder()
                .reviewId(1L)
                .productId(1L)
                .userId(1L)
                .reviewTitle("최소 평점 테스트")
                .rating(1)
                .content("내용")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewService.createReview(any(ReviewCreateRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rating").value(1))
                .andDo(print());

        verify(reviewService, times(1)).createReview(any(ReviewCreateRequest.class));
    }

    @Test
    @DisplayName("경계값 테스트 - 최대 유효 평점")
    void createReview_maximumRating() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .userId(1L)
                .productId(1L)
                .reviewTitle("최대 평점 테스트")
                .rating(5) // 최대값
                .content("내용")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();

        ReviewCreateResponse response = ReviewCreateResponse.builder()
                .reviewId(1L)
                .productId(1L)
                .userId(1L)
                .reviewTitle("최대 평점 테스트")
                .rating(5)
                .content("내용")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewService.createReview(any(ReviewCreateRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andDo(print());

        verify(reviewService, times(1)).createReview(any(ReviewCreateRequest.class));
    }

    @Test
    @DisplayName("음수 사용자 ID로 요청")
    void createReview_negativeUserId() throws Exception {
        // given
        Long userId = -1L;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .userId(userId)
                .productId(1L)
                .reviewTitle("테스트")
                .rating(5)
                .content("내용")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();

        // when & then
        mockMvc.perform(post("/api/users/{userId}/reviews", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());

        verify(reviewService, never()).createReview(any(ReviewCreateRequest.class));
    }

    @Test
    @DisplayName("Content-Type이 없는 요청")
    void createReview_noContentType() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .userId(1L)
                .productId(1L)
                .reviewTitle("테스트")
                .rating(5)
                .content("내용")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();

        // when & then - Content-Type 누락
        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnsupportedMediaType())
                .andDo(print());

        verify(reviewService, never()).createReview(any(ReviewCreateRequest.class));
    }

    @Test
    @DisplayName("빈 내용으로 리뷰 생성 (선택적 필드)")
    void createReview_emptyContent() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .userId(1L)
                .productId(1L)
                .reviewTitle("제목만 있는 리뷰")
                .rating(5)
                .content("") // 빈 내용
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();

        ReviewCreateResponse response = ReviewCreateResponse.builder()
                .reviewId(1L)
                .productId(1L)
                .userId(1L)
                .reviewTitle("제목만 있는 리뷰")
                .rating(5)
                .content("")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewService.createReview(any(ReviewCreateRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value(""))
                .andDo(print());

        verify(reviewService, times(1)).createReview(any(ReviewCreateRequest.class));
    }
}