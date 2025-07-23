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

            if (result instanceof ApiResponse<?> apiResponse) {
                String customMessage = apiResponseFormat.message();
                if (customMessage != null && !customMessage.isEmpty()) {
                    apiResponse.setMessage(customMessage);
                }
                return apiResponse;
            } else {
                String messageToUse = apiResponseFormat.message().isEmpty() ?
                        "요청이 성공했습니다." : apiResponseFormat.message();
                return ApiResponse.success(messageToUse, result);
            }

        } catch (Exception e) {
            log.error("API 실행 중 오류 발생 - Method: {}, Error: {}",
                    joinPoint.getSignature().getName(), e.getMessage());
            throw e;
        }
    }
}