package com.cMall.feedShop.review.validation;

import com.cMall.feedShop.review.application.dto.request.ReviewCreateRequest;
import com.cMall.feedShop.review.application.dto.request.ReviewUpdateRequest;
import com.cMall.feedShop.review.domain.entity.SizeFit;
import com.cMall.feedShop.review.domain.entity.Cushion;
import com.cMall.feedShop.review.domain.entity.Stability;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ReviewValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Validator validator;

    private ReviewCreateRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = ReviewCreateRequest.builder()
                .userId(1L)
                .productId(1L)
                .reviewTitle("유효한 리뷰 제목")
                .rating(5)
                .content("유효한 리뷰 내용입니다.")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Validation: 필수 필드 누락 검증")
    @WithMockUser
    void validateRequiredFields() throws Exception {
        // 1. userId 누락
        ReviewCreateRequest noUserIdRequest = ReviewCreateRequest.builder()
                .productId(1L)
                .reviewTitle("제목")
                .rating(5)
                .content("내용")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noUserIdRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("입력값이 올바르지 않습니다")))
                .andDo(print());

        // 2. productId 누락
        ReviewCreateRequest noProductIdRequest = ReviewCreateRequest.builder()
                .userId(1L)
                .reviewTitle("제목")
                .rating(5)
                .content("내용")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noProductIdRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());

        // 3. rating 누락
        ReviewCreateRequest noRatingRequest = ReviewCreateRequest.builder()
                .userId(1L)
                .productId(1L)
                .reviewTitle("제목")
                .content("내용")
                .sizeFit(SizeFit.PERFECT)
                .cushioning(Cushion.VERY_SOFT)
                .stability(Stability.VERY_STABLE)
                .imageUrls(new ArrayList<>())
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noRatingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName("Validation: 평점 범위 검증 (1-5점)")
    @WithMockUser
    void validateRatingRange() throws Exception {
        // 1. 평점이 0인 경우
        ReviewCreateRequest zeroRatingRequest = validRequest.toBuilder()
                .rating(0)
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zeroRatingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());

        // 2. 평점이 6인 경우
        ReviewCreateRequest sixRatingRequest = validRequest.toBuilder()
                .rating(6)
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sixRatingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());

        // 3. 음수 평점
        ReviewCreateRequest negativeRatingRequest = validRequest.toBuilder()
                .rating(-1)
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(negativeRatingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName("Validation: 텍스트 길이 제한 검증")
    @WithMockUser
    void validateTextLengthLimits() throws Exception {
        // 1. 리뷰 제목 길이 초과 (100자 초과)
        String longTitle = "이것은 매우 긴 리뷰 제목입니다. ".repeat(10); // 100자 초과
        ReviewCreateRequest longTitleRequest = validRequest.toBuilder()
                .reviewTitle(longTitle)
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(longTitleRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());

        // 2. 리뷰 내용 길이 초과 (1000자 초과)
        String longContent = "이것은 매우 긴 리뷰 내용입니다. ".repeat(50); // 1000자 초과
        ReviewCreateRequest longContentRequest = validRequest.toBuilder()
                .content(longContent)
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(longContentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName("Validation: 이미지 URL 개수 제한 검증")
    @WithMockUser
    void validateImageUrlLimit() throws Exception {
        // 6개의 이미지 URL (5개 초과)
        List<String> tooManyImageUrls = List.of(
                "http://example.com/image1.jpg",
                "http://example.com/image2.jpg",
                "http://example.com/image3.jpg",
                "http://example.com/image4.jpg",
                "http://example.com/image5.jpg",
                "http://example.com/image6.jpg"
        );

        ReviewCreateRequest tooManyImagesRequest = validRequest.toBuilder()
                .imageUrls(tooManyImageUrls)
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tooManyImagesRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName("Validation: 양수 값 검증 (ID 필드들)")
    void validatePositiveValues() {
        // 1. 음수 userId
        ReviewCreateRequest negativeUserIdRequest = validRequest.toBuilder()
                .userId(-1L)
                .build();

        Set<ConstraintViolation<ReviewCreateRequest>> violations = validator.validate(negativeUserIdRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("사용자 ID는 양수여야 합니다")));

        // 2. 0인 productId
        ReviewCreateRequest zeroProductIdRequest = validRequest.toBuilder()
                .productId(0L)
                .build();

        violations = validator.validate(zeroProductIdRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("상품 ID는 양수여야 합니다")));
    }

    @Test
    @DisplayName("Validation: enum 값 검증")
    @WithMockUser
    void validateEnumValues() throws Exception {
        // 잘못된 JSON으로 enum 검증 (직접 JSON 문자열 사용)
        String invalidEnumJson = """
                {
                    "userId": 1,
                    "productId": 1,
                    "reviewTitle": "테스트 리뷰",
                    "rating": 5,
                    "content": "테스트 내용",
                    "sizeFit": "INVALID_SIZE",
                    "cushioning": "VERY_SOFT",
                    "stability": "VERY_STABLE",
                    "imageUrls": []
                }
                """;

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEnumJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Validation: 경계값 테스트")
    @WithMockUser
    void validateBoundaryValues() throws Exception {
        // 1. 최소 유효 평점 (1점)
        ReviewCreateRequest minRatingRequest = validRequest.toBuilder()
                .rating(1)
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minRatingRequest)))
                .andExpect(status().isOk())
                .andDo(print());

        // 2. 최대 유효 평점 (5점)
        ReviewCreateRequest maxRatingRequest = validRequest.toBuilder()
                .rating(5)
                .userId(2L) // 중복 방지를 위해 다른 사용자
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 2L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maxRatingRequest)))
                .andExpect(status().isOk())
                .andDo(print());

        // 3. 최대 길이 제목 (100자 정확히)
        String maxLengthTitle = "a".repeat(100);
        ReviewCreateRequest maxTitleRequest = validRequest.toBuilder()
                .reviewTitle(maxLengthTitle)
                .userId(3L)
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 3L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maxTitleRequest)))
                .andExpect(status().isOk())
                .andDo(print());

        // 4. 최대 개수 이미지 (5개 정확히)
        List<String> maxImageUrls = List.of(
                "http://example.com/image1.jpg",
                "http://example.com/image2.jpg",
                "http://example.com/image3.jpg",
                "http://example.com/image4.jpg",
                "http://example.com/image5.jpg"
        );

        ReviewCreateRequest maxImagesRequest = validRequest.toBuilder()
                .imageUrls(maxImageUrls)
                .userId(4L)
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 4L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maxImagesRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Validation: 업데이트 요청 검증")
    void validateUpdateRequest() {
        // 잘못된 업데이트 요청들

        // 1. 평점 범위 초과
        ReviewUpdateRequest invalidRatingUpdate = ReviewUpdateRequest.builder()
                .rating(10)
                .build();

        Set<ConstraintViolation<ReviewUpdateRequest>> violations = validator.validate(invalidRatingUpdate);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("평점은 5점 이하여야 합니다")));

        // 2. 제목 길이 초과
        String longTitle = "a".repeat(101);
        ReviewUpdateRequest longTitleUpdate = ReviewUpdateRequest.builder()
                .reviewTitle(longTitle)
                .build();

        violations = validator.validate(longTitleUpdate);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("리뷰 제목은 100자를 초과할 수 없습니다")));
    }

    @Test
    @DisplayName("Validation: 빈 값과 null 값 처리")
    @WithMockUser
    void validateEmptyAndNullValues() throws Exception {
        // 1. 빈 문자열 제목
        ReviewCreateRequest emptyTitleRequest = validRequest.toBuilder()
                .reviewTitle("")
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyTitleRequest)))
                .andExpect(status().isOk()) // 빈 제목은 허용 (선택적 필드)
                .andDo(print());

        // 2. null 제목 (선택적 필드이므로 허용)
        ReviewCreateRequest nullTitleRequest = validRequest.toBuilder()
                .reviewTitle(null)
                .userId(2L)
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 2L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullTitleRequest)))
                .andExpect(status().isOk())
                .andDo(print());

        // 3. 빈 이미지 리스트 (허용)
        ReviewCreateRequest emptyImageListRequest = validRequest.toBuilder()
                .imageUrls(Collections.emptyList())
                .userId(3L)
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 3L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyImageListRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Validation: 특수 문자 및 유니코드 처리")
    @WithMockUser
    void validateSpecialCharactersAndUnicode() throws Exception {
        // 1. 특수 문자가 포함된 제목
        ReviewCreateRequest specialCharsRequest = validRequest.toBuilder()
                .reviewTitle("특수문자 테스트! @#$%^&*()_+-={}[]|\\:;\"'<>?,./")
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(specialCharsRequest)))
                .andExpect(status().isOk())
                .andDo(print());

        // 2. 유니코드 문자 (이모지 포함)
        ReviewCreateRequest unicodeRequest = validRequest.toBuilder()
                .reviewTitle("완벽한 신발 😍👟✨")
                .content("정말 좋아요! 💯 추천합니다 👍")
                .userId(2L)
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 2L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unicodeRequest)))
                .andExpect(status().isOk())
                .andDo(print());

        // 3. 다양한 언어 (한글, 영어, 일본어, 중국어)
        ReviewCreateRequest multiLanguageRequest = validRequest.toBuilder()
                .reviewTitle("Perfect shoes 完璧な靴 完美的鞋子")
                .content("한글 English 日本語 中文 모두 지원되는지 테스트")
                .userId(3L)
                .build();

        mockMvc.perform(post("/api/users/{userId}/reviews", 3L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(multiLanguageRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}