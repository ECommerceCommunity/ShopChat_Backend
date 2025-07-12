package com.cMall.feedShop.review.application.exception;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;

public class ReviewException extends BusinessException {
    
    public ReviewException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public ReviewException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    // 미리 정의된 예외들 (ErrorCode 기반)
    public static class ReviewNotFoundException extends ReviewException {
        public ReviewNotFoundException() {
            super(ErrorCode.REVIEW_NOT_FOUND);
        }
    }
    
    public static class ReviewAlreadyExistsException extends ReviewException {
        public ReviewAlreadyExistsException() {
            super(ErrorCode.REVIEW_ALREADY_EXISTS);
        }
    }
    
    public static class ReviewAccessDeniedException extends ReviewException {
        public ReviewAccessDeniedException() {
            super(ErrorCode.REVIEW_ACCESS_DENIED);
        }
    }
    
    public static class InvalidRatingException extends ReviewException {
        public InvalidRatingException() {
            super(ErrorCode.INVALID_RATING);
        }
    }
    
    public static class ReviewContentTooLongException extends ReviewException {
        public ReviewContentTooLongException() {
            super(ErrorCode.REVIEW_CONTENT_TOO_LONG);
        }
    }
}