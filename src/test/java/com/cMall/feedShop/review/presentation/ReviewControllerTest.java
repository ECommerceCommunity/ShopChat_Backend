package com.cMall.feedShop.review.presentation;

import com.cMall.feedShop.review.application.service.ReviewService;
import com.cMall.feedShop.user.infrastructure.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ReviewController 테스트 (로그인 불필요)")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("컨텍스트 로딩 테스트")
    void contextLoads() {
        assert mockMvc != null;
        assert reviewService != null;
        assert jwtTokenProvider != null;
    }

    @Test
    @DisplayName("상품별 리뷰 목록 조회 - 기본 파라미터")
    void getProductReviews_withDefaultParams_returnsOk() throws Exception {
        Long productId = 1L;

        mockMvc.perform(get("/api/reviews/products/{productId}", productId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("요청이 성공했습니다."));
    }

    @Test
    @DisplayName("상품별 리뷰 목록 조회 - 커스텀 파라미터")
    void getProductReviews_withCustomParams_returnsOk() throws Exception {
        Long productId = 1L;

        mockMvc.perform(get("/api/reviews/products/{productId}", productId)
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "points"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("요청이 성공했습니다."));
    }

    @Test
    @DisplayName("상품별 리뷰 목록 조회 - 최신순 정렬")
    void getProductReviews_withLatestSort_returnsOk() throws Exception {
        Long productId = 1L;

        mockMvc.perform(get("/api/reviews/products/{productId}", productId)
                        .param("sort", "latest"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("상품별 리뷰 목록 조회 - 인기순 정렬")
    void getProductReviews_withPointsSort_returnsOk() throws Exception {
        Long productId = 1L;

        mockMvc.perform(get("/api/reviews/products/{productId}", productId)
                        .param("sort", "points"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("리뷰 상세 조회 - 정상 요청")
    void getReview_withValidId_returnsOk() throws Exception {
        Long reviewId = 1L;

        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("요청이 성공했습니다."));
    }

    @Test
    @DisplayName("존재하지 않는 상품의 리뷰 조회")
    void getProductReviews_nonExistentProduct_returnsAppropriateResponse() throws Exception {
        Long nonExistentProductId = 999999L;

        mockMvc.perform(get("/api/reviews/products/{productId}", nonExistentProductId))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 200 && status != 404 && status != 500) {
                        throw new AssertionError("Expected status 200, 404, or 500 but was " + status);
                    }
                });
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 조회")
    void getReview_nonExistentReview_returnsAppropriateResponse() throws Exception {
        Long nonExistentReviewId = 999999L;

        mockMvc.perform(get("/api/reviews/{reviewId}", nonExistentReviewId))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 200 && status != 404 && status != 500) {
                        throw new AssertionError("Expected status 200, 404, or 500 but was " + status);
                    }
                });
    }

    @Test
    @DisplayName("잘못된 상품 ID 형식 (문자열)")
    void getProductReviews_invalidProductIdFormat_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reviews/products/invalid"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 리뷰 ID 형식 (문자열)")
    void getReview_invalidReviewIdFormat_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reviews/invalid"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 엔드포인트 접근")
    void accessNonExistentEndpoint_returnsAppropriateResponse() throws Exception {
        mockMvc.perform(get("/api/reviews/nonexistent"))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 400 && status != 404 && status != 500) {
                        throw new AssertionError("Expected status 400, 404, or 500 but was " + status);
                    }
                });
    }

    @Test
    @DisplayName("지원하지 않는 HTTP 메서드")
    void unsupportedHttpMethod_returnsAppropriateResponse() throws Exception {
        mockMvc.perform(patch("/api/reviews/products/1"))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 405 && status != 500) {
                        throw new AssertionError("Expected status 405 or 500 but was " + status);
                    }
                });
    }

    @Test
    @DisplayName("페이지 번호 음수 값 테스트")
    void getProductReviews_withNegativePage_returnsAppropriateResponse() throws Exception {
        Long productId = 1L;

        mockMvc.perform(get("/api/reviews/products/{productId}", productId)
                        .param("page", "-1"))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 200 && status != 400 && status != 500) {
                        throw new AssertionError("Expected status 200, 400, or 500 but was " + status);
                    }
                });
    }

    @Test
    @DisplayName("페이지 크기 0 값 테스트")
    void getProductReviews_withZeroSize_returnsAppropriateResponse() throws Exception {
        Long productId = 1L;

        mockMvc.perform(get("/api/reviews/products/{productId}", productId)
                        .param("size", "0"))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 200 && status != 400 && status != 500) {
                        throw new AssertionError("Expected status 200, 400, or 500 but was " + status);
                    }
                });
    }

    @Test
    @DisplayName("잘못된 정렬 파라미터 테스트")
    void getProductReviews_withInvalidSortParam_returnsAppropriateResponse() throws Exception {
        Long productId = 1L;

        mockMvc.perform(get("/api/reviews/products/{productId}", productId)
                        .param("sort", "invalid"))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 200 && status != 400 && status != 500) {
                        throw new AssertionError("Expected status 200, 400, or 500 but was " + status);
                    }
                });
    }
}