package com.cMall.feedShop.review.integration;

import com.cMall.feedShop.review.application.dto.request.ReviewCreateRequest;
import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.Stability;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ReviewApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("API Integration: 리뷰 상세 조회 워크플로우")
    @WithMockUser
    void reviewDetailWorkflow() throws Exception {
        // 1. CREATE - 리뷰 생성
        ReviewCreateRequest createRequest = ReviewCreateRequest.builder()
                .userId(1L)
                .productId(1L)
                .reviewTitle("API 테스트 리뷰")
                .rating(5)
                .content("완벽한 신발입니다. 5단계 평가 모두 최고!")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reviewId").exists())
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.sizeFit").value("PERFECT"))
                .andExpect(jsonPath("$.data.cushioning").value("VERY_SOFT"))
                .andExpect(jsonPath("$.data.stability").value("VERY_STABLE"))
                .andDo(print())
                .andReturn();

        // 생성된 리뷰 ID 추출
        String responseContent = createResult.getResponse().getContentAsString();
        Long reviewId = objectMapper.readTree(responseContent).get("data").get("reviewId").asLong();

        // 2. READ - 리뷰 상세 조회
        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reviewId").value(reviewId))
                .andExpect(jsonPath("$.data.reviewTitle").value("API 테스트 리뷰"))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.sizeFit").value("PERFECT"))
                .andExpect(jsonPath("$.data.cushioning").value("VERY_SOFT"))
                .andExpect(jsonPath("$.data.stability").value("VERY_STABLE"))
                .andDo(print());
    }

    @Test
    @DisplayName("API Integration: 상품별 리뷰 조회")
    @WithMockUser
    void productReviewsWorkflow() throws Exception {
        Long productId = 1L;

        // 다양한 평점의 리뷰들 생성
        createReviewViaApi(1L, productId, 5, SizeFit.PERFECT, Cushion.VERY_SOFT, Stability.VERY_STABLE);
        createReviewViaApi(2L, productId, 4, SizeFit.PERFECT, Cushion.SOFT, Stability.STABLE);
        createReviewViaApi(3L, productId, 3, SizeFit.BIG, Cushion.NORMAL, Stability.NORMAL);

        // 상품 리뷰 목록 조회
        mockMvc.perform(get("/api/products/{productId}/reviews", productId)
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.totalReviews").value(3))
                .andExpect(jsonPath("$.recentReviews").isArray())
                .andDo(print());
    }

    @Test
    @DisplayName("API Integration: 에러 케이스 처리")
    @WithMockUser
    void errorCasesHandling() throws Exception {
        // 1. 존재하지 않는 리뷰 조회
        mockMvc.perform(get("/api/reviews/{reviewId}", 99999L)
                        .with(csrf()))
                .andExpect(status().isInternalServerError()) // GlobalExceptionHandler에 의해 500으로 처리
                .andDo(print());

        // 2. 중복 리뷰 생성 시도
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .userId(1L)
                .productId(1L)
                .reviewTitle("첫 번째 리뷰")
                .rating(5)
                .content("완벽한 신발")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();

        // 첫 번째 리뷰 생성
        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 같은 사용자가 같은 상품에 중복 리뷰 시도
        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError()) // GlobalExceptionHandler에 의해 500으로 처리
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("서버 오류가 발생했습니다")))
                .andDo(print());
    }

    @Test
    @DisplayName("API Integration: 유효성 검사 에러")
    @WithMockUser
    void validationErrorHandling() throws Exception {
        // 잘못된 평점으로 리뷰 생성 시도
        ReviewCreateRequest invalidRequest = ReviewCreateRequest.builder()
                .userId(1L)
                .productId(1L)
                .reviewTitle("잘못된 리뷰")
                .rating(10) // 잘못된 평점 (1-5 범위 초과)
                .content("테스트 내용")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("입력값이 올바르지 않습니다")))
                .andDo(print());
    }

    // 헬퍼 메서드
    private void createReviewViaApi(Long userId, Long productId, int rating,
                                    SizeFit sizeFit, Cushion cushioning, Stability stability) throws Exception {
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .userId(userId)
                .productId(productId)
                .reviewTitle("테스트 리뷰 " + userId)
                .rating(rating)
                .content("평점 " + rating + "점 리뷰입니다")
                .sizeFit(sizeFit)
                .cushioning(cushioning)
                .stability(stability)
                .imageUrls(new ArrayList<>())
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}