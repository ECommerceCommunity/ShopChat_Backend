// Event 도메인 전용 예외 핸들러
package com.cMall.feedShop.event.application.exception;

import com.cMall.feedShop.common.dto.ApiResponse;
import com.cMall.feedShop.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.cMall.feedShop.event")
@Order(Ordered.HIGHEST_PRECEDENCE) // GlobalExceptionHandler보다 우선 처리
public class EventExceptionHandler {

    // 이벤트를 찾을 수 없음
    @ExceptionHandler(EventException.EventNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEventNotFoundException(
            EventException.EventNotFoundException exception, HttpServletRequest request) {
        ErrorCode errorCode = exception.getErrorCode();
        
        log.warn("Event 조회 실패 - URI: {}, Method: {}, ErrorCode: {}, ErrorMessage: {}",
                request.getRequestURI(), 
                request.getMethod(),
                errorCode.getCode(), 
                errorCode.getMessage());

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message(errorCode.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    // 유효하지 않은 이벤트 상태
    @ExceptionHandler(EventException.InvalidEventStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidEventStatusException(
            EventException.InvalidEventStatusException exception, HttpServletRequest request) {
        ErrorCode errorCode = exception.getErrorCode();
        
        log.warn("유효하지 않은 이벤트 상태 - URI: {}, Method: {}, ErrorCode: {}, ErrorMessage: {}",
                request.getRequestURI(), 
                request.getMethod(),
                errorCode.getCode(), 
                errorCode.getMessage());

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message(errorCode.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    // 유효하지 않은 이벤트 타입
    @ExceptionHandler(EventException.InvalidEventTypeException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidEventTypeException(
            EventException.InvalidEventTypeException exception, HttpServletRequest request) {
        ErrorCode errorCode = exception.getErrorCode();
        
        log.warn("유효하지 않은 이벤트 타입 - URI: {}, Method: {}, ErrorCode: {}, ErrorMessage: {}",
                request.getRequestURI(), 
                request.getMethod(),
                errorCode.getCode(), 
                errorCode.getMessage());

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message(errorCode.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
} 