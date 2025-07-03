package com.cMall.feedShop.common.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorResponse {
    private final boolean success = false;
    private final String code;
    private final String message;
    private final String timestamp;
    private final List<FieldError> fieldErrors;

    // 생성자
    private ErrorResponse(String code, String message, String timestamp, List<FieldError> fieldErrors) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.fieldErrors = fieldErrors;
    }

    // 팩토리 메서드 (필드 에러 없음)
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                LocalDateTime.now().toString(),
                new ArrayList<>()
        );
    }

    // 팩토리 메서드 (필드 에러 포함)
    public static ErrorResponse of(ErrorCode errorCode, List<FieldError> fieldErrors) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                LocalDateTime.now().toString(),
                fieldErrors
        );
    }

    // 내부 클래스
    @Getter
    public static class FieldError {
        private final String field;
        private final String value;
        private final String reason;

        private FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        // 팩토리 메서드
        public static FieldError of(String field, String value, String reason) {
            return new FieldError(field, value, reason);
        }
    }
}
