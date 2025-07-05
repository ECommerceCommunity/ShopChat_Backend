package com.cMall.feedShop.cart.application.exception;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;

public class CartItemException extends BusinessException {
    public CartItemException(ErrorCode errorCode) {
        super(errorCode);
    }
}
