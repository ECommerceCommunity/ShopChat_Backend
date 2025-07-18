// Event 도메인 전용 예외 클래스
package com.cMall.feedShop.event.application.exception;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;

public class EventException {

    // 이벤트를 찾을 수 없음
    public static class EventNotFoundException extends BusinessException {
        public EventNotFoundException() {
            super(ErrorCode.EVENT_NOT_FOUND);
        }
    }

    // 유효하지 않은 이벤트 상태
    public static class InvalidEventStatusException extends BusinessException {
        public InvalidEventStatusException() {
            super(ErrorCode.INVALID_EVENT_STATUS);
        }
    }

    // 유효하지 않은 이벤트 타입
    public static class InvalidEventTypeException extends BusinessException {
        public InvalidEventTypeException() {
            super(ErrorCode.INVALID_EVENT_TYPE);
        }
    }
} 