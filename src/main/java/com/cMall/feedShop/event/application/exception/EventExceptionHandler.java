// Event 도메인 전용 예외 핸들러
package com.cMall.feedShop.event.application.exception;

import com.cMall.feedShop.common.dto.ApiResponse;
import com.cMall.feedShop.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.cMall.feedShop.event")
public class EventExceptionHandler {

    @ExceptionHandler(EventException.class)
    public ResponseEntity<ApiResponse<Void>> handleEventException(
            EventException exception, HttpServletRequest request) {
        ErrorCode errorCode = exception.getErrorCode();
        log.warn("EventException 발생 : URI={}, Method={}, ErrorCode={}, ErrorMessage={}",
                request.getRequestURI(), request.getMethod(),
                errorCode.getCode(), errorCode.getMessage());

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message(errorCode.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
} 