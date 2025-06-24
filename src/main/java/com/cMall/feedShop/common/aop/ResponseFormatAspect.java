package com.cMall.feedShop.common.aop;

import com.cMall.feedShop.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ResponseFormatAspect {

    @Around("@annotation(apiResponseFormat)")
    public Object formatResponse(ProceedingJoinPoint joinPoint, ApiResponseFormat apiResponseFormat) throws Throwable {
        try {
            Object result = joinPoint.proceed();

            // 이미 ApiResponse 타입이면 그대로 반환
            if (result instanceof ApiResponse) {
                return result;
            }

            // 성공 응답으로 래핑
            String message = apiResponseFormat.message().isEmpty() ?
                    "요청이 성공했습니다." : apiResponseFormat.message();

            return ApiResponse.success(message, result);

        } catch (Exception e) {
            log.error("API 실행 중 오류 발생 - Method: {}, Error: {}",
                    joinPoint.getSignature().getName(), e.getMessage());
            throw e; // 예외는 GlobalExceptionHandler에서 처리
        }
    }
}