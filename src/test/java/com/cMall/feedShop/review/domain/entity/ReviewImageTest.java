package com.cMall.feedShop.review.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ReviewImageTest {

    @Test
    @DisplayName("Given image info_When create ReviewImage_Then all fields are set correctly")
    void givenImageInfo_whenCreateReviewImage_thenAllFieldsAreSetCorrectly() {
        // given
        Long reviewId = 1L;
        String imageUrl = "https://example.com/review-image.jpg";
        Integer imageOrder = 1;

        // when
        ReviewImage reviewImage = ReviewImage.builder()
                .reviewId(reviewId)
                .imageUrl(imageUrl)
                .imageOrder(imageOrder)
                .build();

        // then
        assertNotNull(reviewImage);
        assertEquals(reviewId, reviewImage.getReviewId());
        assertEquals(imageUrl, reviewImage.getImageUrl());
        assertEquals(imageOrder, reviewImage.getImageOrder());
    }

    @Test
    @DisplayName("Given multiple images_When create with different orders_Then orders are set correctly")
    void givenMultipleImages_whenCreateWithDifferentOrders_thenOrdersAreSetCorrectly() {
        // given
        Long reviewId = 1L;

        // when
        ReviewImage firstImage = ReviewImage.builder()
                .reviewId(reviewId)
                .imageUrl("https://example.com/image1.jpg")
                .imageOrder(1)
                .build();

        ReviewImage secondImage = ReviewImage.builder()
                .reviewId(reviewId)
                .imageUrl("https://example.com/image2.jpg")
                .imageOrder(2)
                .build();

        ReviewImage thirdImage = ReviewImage.builder()
                .reviewId(reviewId)
                .imageUrl("https://example.com/image3.jpg")
                .imageOrder(3)
                .build();

        // then
        assertEquals(1, firstImage.getImageOrder());
        assertEquals(2, secondImage.getImageOrder());
        assertEquals(3, thirdImage.getImageOrder());

        // 모든 이미지가 같은 리뷰에 속하는지 확인
        assertEquals(reviewId, firstImage.getReviewId());
        assertEquals(reviewId, secondImage.getReviewId());
        assertEquals(reviewId, thirdImage.getReviewId());
    }

    @Test
    @DisplayName("Given valid image data_When create ReviewImage_Then no exception is thrown")
    void givenValidImageData_whenCreateReviewImage_thenNoExceptionIsThrown() {
        // given & when & then
        assertDoesNotThrow(() -> {
            ReviewImage reviewImage = ReviewImage.builder()
                    .reviewId(1L)
                    .imageUrl("https://cdn.example.com/shoe-review.png")
                    .imageOrder(1)
                    .build();

            assertNotNull(reviewImage.getReviewId());
            assertNotNull(reviewImage.getImageUrl());
            assertNotNull(reviewImage.getImageOrder());
        });
    }

    @Test
    @DisplayName("Given image order zero_When create ReviewImage_Then order is set to zero")
    void givenImageOrderZero_whenCreateReviewImage_thenOrderIsSetToZero() {
        // given
        Integer imageOrder = 0;

        // when
        ReviewImage reviewImage = ReviewImage.builder()
                .reviewId(1L)
                .imageUrl("https://example.com/test.jpg")
                .imageOrder(imageOrder)
                .build();

        // then
        assertEquals(0, reviewImage.getImageOrder());
    }
}