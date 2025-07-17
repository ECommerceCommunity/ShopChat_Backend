/*package com.cMall.feedShop.review.application.service;

import com.cMall.feedShop.review.application.ReviewImageService;
import com.cMall.feedShop.review.domain.entity.ReviewImage;
import com.cMall.feedShop.review.domain.repository.ReviewImageRepository;
import com.cMall.feedShop.review.application.dto.request.ReviewImageOrderRequest;
import com.cMall.feedShop.review.application.dto.response.ReviewImageUploadResponse;
import com.cMall.feedShop.review.application.dto.response.ReviewImageOrderResponse;
import com.cMall.feedShop.review.application.exception.ReviewException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ReviewImageServiceTest {

    @Mock
    private ReviewImageRepository reviewImageRepository;

    @InjectMocks
    private ReviewImageService reviewImageService;

    @Test
    @DisplayName("Given valid image file_When upload image_Then return upload response")
    void givenValidImageFile_whenUploadImage_thenReturnUploadResponse() {
        // given
        Long reviewId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("cushion.jpg");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn("image/jpeg");

        ReviewImage savedImage = ReviewImage.builder()
                .imageId(1L)
                .reviewId(reviewId)
                .imageUrl("https://s3.example.com/cushion.jpg")
                .originalName("cushion.jpg")
                .displayOrder(1)
                .build();

        when(reviewImageRepository.save(any(ReviewImage.class))).thenReturn(savedImage);

        // when
        ReviewImageUploadResponse response = reviewImageService.uploadImage(reviewId, mockFile);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getImageId());
        assertEquals("https://s3.example.com/cushion.jpg", response.getImageUrl());
        verify(reviewImageRepository, times(1)).save(any(ReviewImage.class));
    }

    @Test
    @DisplayName("Given invalid file type_When upload image_Then throw exception")
    void givenInvalidFileType_whenUploadImage_thenThrowException() {
        // given
        Long reviewId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getContentType()).thenReturn("text/plain");

        // when & then
        assertThrows(ReviewException.class, () -> {
            reviewImageService.uploadImage(reviewId, mockFile);
        });
    }

    @Test
    @DisplayName("Given order request_When update image order_Then return order response")
    void givenOrderRequest_whenUpdateImageOrder_thenReturnOrderResponse() {
        // given
        Long reviewId = 1L;
        ReviewImageOrderRequest orderRequest = ReviewImageOrderRequest.builder()
                .imageOrders(List.of(
                        ReviewImageOrderRequest.ImageOrder.builder().imageId(1L).displayOrder(2).build(),
                        ReviewImageOrderRequest.ImageOrder.builder().imageId(2L).displayOrder(1).build()
                ))
                .build();

        List<ReviewImage> images = List.of(
                ReviewImage.builder().id(1L).reviewId(reviewId).displayOrder(2).build(),
                ReviewImage.builder().id(2L).reviewId(reviewId).displayOrder(1).build()
        );

        when(reviewImageRepository.findByReviewIdOrderByDisplayOrder(reviewId)).thenReturn(images);

        // when
        ReviewImageOrderResponse response = reviewImageService.updateImageOrder(reviewId, orderRequest);

        // then
        assertNotNull(response);
        assertEquals(2, response.getUpdatedImages().size());
        verify(reviewImageRepository, times(1)).saveAll(any());
    }
}*/