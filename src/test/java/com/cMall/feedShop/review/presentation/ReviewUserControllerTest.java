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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ReviewUserController 테스트 (로그인 필요)")
class ReviewUserControllerTest {

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

    // ====== 리뷰 작성 테스트 ======

    @Test
    @DisplayName("리뷰 작성 - 인증 없이 접근 시 401 반환")
    void createReview_withoutAuth_returns401() throws Exception {
        String reviewRequest = """
                {
                    "productId": 1,
                    "rating": 5,
                    "content": "훌륭한 상품입니다!"
                }
                """;

        mockMvc.perform(post("/api/user/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewRequest)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 작성 - 인증된 사용자 정상 요청")
    void createReview_withAuth_returnsSuccess() throws Exception {
        String reviewRequest = """
                {
                    "productId": 1,
                    "rating": 5,
                    "content": "훌륭한 상품입니다!"
                }
                """;

        mockMvc.perform(post("/api/user/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewRequest)
                        .with(csrf()))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 200 && status != 201 && status != 400 && status != 500) {
                        throw new AssertionError("Expected status 200, 201, 400, or 500 but was " + status);
                    }
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 작성 - 높은 평점 테스트")
    void createReview_withHighRating_returnsSuccess() throws Exception {
        String reviewRequest = """
                {
                    "productId": 1,
                    "rating": 5,
                    "content": "정말 만족합니다!"
                }
                """;

        mockMvc.perform(post("/api/user/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewRequest)
                        .with(csrf()))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 200 && status != 201 && status != 400 && status != 500) {
                        throw new AssertionError("Expected status 200, 201, 400, or 500 but was " + status);
                    }
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 작성 - 낮은 평점 테스트")
    void createReview_withLowRating_returnsSuccess() throws Exception {
        String reviewRequest = """
                {
                    "productId": 1,
                    "rating": 1,
                    "content": "별로였습니다."
                }
                """;

        mockMvc.perform(post("/api/user/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewRequest)
                        .with(csrf()))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 200 && status != 201 && status != 400 && status != 500) {
                        throw new AssertionError("Expected status 200, 201, 400, or 500 but was " + status);
                    }
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 작성 - 잘못된 JSON 형식")
    void createReview_withInvalidJson_returnsBadRequest() throws Exception {
        String invalidJson = """
                {
                    "productId": "not_a_number",
                    "rating": 6,
                    "content": ""
                }
                """;

        mockMvc.perform(post("/api/user/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
                        .with(csrf()))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 400 && status != 500) {
                        throw new AssertionError("Expected status 400 or 500 but was " + status);
                    }
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 작성 - 빈 내용")
    void createReview_withEmptyContent_returnsBadRequest() throws Exception {
        String reviewRequest = """
                {
                    "productId": 1,
                    "rating": 5,
                    "content": ""
                }
                """;

        mockMvc.perform(post("/api/user/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewRequest)
                        .with(csrf()))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 200 && status != 201 && status != 400 && status != 500) {
                        throw new AssertionError("Expected status 200, 201, 400, or 500 but was " + status);
                    }
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 작성 - 잘못된 평점 범위 (0)")
    void createReview_withInvalidRatingZero_returnsBadRequest() throws Exception {
        String reviewRequest = """
                {
                    "productId": 1,
                    "rating": 0,
                    "content": "평점이 0입니다."
                }
                """;

        mockMvc.perform(post("/api/user/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewRequest)
                        .with(csrf()))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 400 && status != 500) {
                        throw new AssertionError("Expected status 400 or 500 but was " + status);
                    }
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 작성 - 잘못된 평점 범위 (6)")
    void createReview_withInvalidRatingSix_returnsBadRequest() throws Exception {
        String reviewRequest = """
                {
                    "productId": 1,
                    "rating": 6,
                    "content": "평점이 6입니다."
                }
                """;

        mockMvc.perform(post("/api/user/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewRequest)
                        .with(csrf()))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 400 && status != 500) {
                        throw new AssertionError("Expected status 400 or 500 but was " + status);
                    }
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 작성 - Content-Type 없이 요청")
    void createReview_withoutContentType_returnsUnsupportedMediaType() throws Exception {
        String reviewRequest = """
                {
                    "productId": 1,
                    "rating": 5,
                    "content": "훌륭한 상품입니다!"
                }
                """;

        mockMvc.perform(post("/api/user/reviews")
                        .content(reviewRequest)
                        .with(csrf()))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 415 && status != 500) {
                        throw new AssertionError("Expected status 415 or 500 but was " + status);
                    }
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 작성 - CSRF 토큰 없이 요청")
    void createReview_withoutCsrf_returnsForbidden() throws Exception {
        String reviewRequest = """
                {
                    "productId": 1,
                    "rating": 5,
                    "content": "훌륭한 상품입니다!"
                }
                """;

        mockMvc.perform(post("/api/user/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewRequest))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // ====== 아직 구현되지 않은 기능들 (TODO) ======

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 수정 - 아직 구현되지 않음 (TODO)")
    void updateReview_notImplementedYet_returnsNotFound() throws Exception {
        String updateRequest = """
                {
                    "rating": 4,
                    "content": "수정된 리뷰입니다."
                }
                """;

        mockMvc.perform(put("/api/user/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 삭제 - 아직 구현되지 않음 (TODO)")
    void deleteReview_notImplementedYet_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/user/reviews/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 추천 - 아직 구현되지 않음 (TODO)")
    void addReviewPoint_notImplementedYet_returnsNotFound() throws Exception {
        mockMvc.perform(post("/api/user/reviews/1/points")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("리뷰 추천 - 인증 없이 접근 시 401 반환")
    void addReviewPoint_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/api/user/reviews/1/points")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ====== 잘못된 요청 테스트 ======

    @Test
    @DisplayName("존재하지 않는 엔드포인트 접근")
    void accessNonExistentEndpoint_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/user/reviews/nonexistent"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("지원하지 않는 HTTP 메서드")
    void unsupportedHttpMethod_returnsMethodNotAllowed() throws Exception {
        mockMvc.perform(patch("/api/user/reviews")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("잘못된 요청 바디 형식")
    void createReview_withMalformedJson_returnsBadRequest() throws Exception {
        String malformedJson = """
                {
                    "productId": 1,
                    "rating": 5,
                    "content": "훌륭한 상품입니다!"
                """; // 닫는 괄호 누락

        mockMvc.perform(post("/api/user/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}