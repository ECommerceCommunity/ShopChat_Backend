package com.cMall.feedShop.order.application.exception;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;

public class OrderException extends BusinessException {
    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
