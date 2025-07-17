package com.cMall.feedShop.review.presentation;

import com.cMall.feedShop.review.application.ReviewService;
import com.cMall.feedShop.review.application.dto.response.ProductReviewSummaryResponse;
import com.cMall.feedShop.review.application.dto.response.ReviewSummaryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

// @WebMvcTest 대신 @SpringBootTest 사용하여 전체 컨텍스트 로드
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=test_jwt_secret_key_for_testing_which_should_be_at_least_256_bit_long_string"
})
public class ReviewProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Test
    @DisplayName("상품별 리뷰 목록 조회 API 테스트 - 디버깅")
    void getProductReviews_debug() throws Exception {
        // given
        Long productId = 1L;

        List<ReviewSummaryResponse> recentReviews = List.of(
                ReviewSummaryResponse.builder()
                        .reviewId(1L)
                        .userId(1L)
                        .productId(productId)
                        .reviewTitle("정말 좋아요!")
                        .content("최고예요!")
                        .rating(5)
                        .createdAt(LocalDateTime.now())
                        .images(new ArrayList<>())
                        .build()
        );

        ProductReviewSummaryResponse.RatingDistribution ratingDistribution =
                ProductReviewSummaryResponse.RatingDistribution.builder()
                        .fiveStar(15L)
                        .fourStar(8L)
                        .threeStar(2L)
                        .twoStar(0L)
                        .oneStar(0L)
                        .build();

        ProductReviewSummaryResponse response = ProductReviewSummaryResponse.builder()
                .productId(productId)
                .averageRating(4.5)
                .totalReviews(25L)
                .ratingDistribution(ratingDistribution)
                .mostCommonSizeFit("PERFECT")
                .recentReviews(recentReviews)
                .build();

        when(reviewService.getProductReviews(eq(productId), any())).thenReturn(response);

        // when & then - 에러 내용을 자세히 확인
        MvcResult result = mockMvc.perform(get("/api/products/{productId}/reviews", productId)
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print()) // 응답 내용 출력
                .andReturn();

        // 응답 내용 확인
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Content: " + result.getResponse().getContentAsString());
        System.out.println("Error Message: " + result.getResponse().getErrorMessage());

        // 실제 상태 코드가 어떤지 확인
        // .andExpect(status().isOk())
    }

    @Test
    @DisplayName("잘못된 페이지 파라미터 디버깅")
    void getProductReviews_invalidPageParam_debug() throws Exception {
        // given
        Long productId = 1L;

        // when & then - 에러 내용을 자세히 확인
        MvcResult result = mockMvc.perform(get("/api/products/{productId}/reviews", productId)
                        .param("page", "invalid")
                        .param("size", "10"))
                .andDo(print()) // 응답 내용 출력
                .andReturn();

        // 응답 내용 확인
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Content: " + result.getResponse().getContentAsString());
        System.out.println("Error Message: " + result.getResponse().getErrorMessage());
    }

    @Test
    @DisplayName("음수 페이지 크기 디버깅")
    void getProductReviews_negativeSizeParam_debug() throws Exception {
        // given
        Long productId = 1L;

        // when & then - 에러 내용을 자세히 확인
        MvcResult result = mockMvc.perform(get("/api/products/{productId}/reviews", productId)
                        .param("page", "0")
                        .param("size", "-1"))
                .andDo(print()) // 응답 내용 출력
                .andReturn();

        // 응답 내용 확인
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Content: " + result.getResponse().getContentAsString());
        System.out.println("Error Message: " + result.getResponse().getErrorMessage());
    }
}