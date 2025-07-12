// Event 도메인 전용 예외 클래스
package com.cMall.feedShop.event.application.exception;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;

public class EventException extends BusinessException {
    public EventException(ErrorCode errorCode) {
        super(errorCode);
    }
} 