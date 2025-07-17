package com.cMall.feedShop.review.application.exception;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;

public class ReviewException {

    public static class ReviewNotFoundException extends BusinessException {
        public ReviewNotFoundException() {
            super(ErrorCode.valueOf("REVIEW_NOT_FOUND"), "리뷰를 찾을 수 없습니다.");
        }

        public ReviewNotFoundException(String message) {
            super(ErrorCode.valueOf("REVIEW_NOT_FOUND"), message);
        }
    }

    public static class DuplicateReviewException extends BusinessException {
        public DuplicateReviewException() {
            super(ErrorCode.valueOf("DUPLICATE_REVIEW"), "이미 해당 상품에 대한 리뷰를 작성하셨습니다.");
        }
    }

    public static class InvalidReviewDataException extends BusinessException {
        public InvalidReviewDataException(String message) {
            super(ErrorCode.INVALID_INPUT_VALUE, message);
        }
    }
    public static class ReviewAccessDeniedException extends BusinessException {
        public ReviewAccessDeniedException() {
            super(ErrorCode.FORBIDDEN, "리뷰에 대한 권한이 없습니다.");
        }
    }
}