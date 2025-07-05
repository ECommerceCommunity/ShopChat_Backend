package com.cMall.feedShop.store.application.exception;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;

public class StoreException extends BusinessException {
    public StoreException(ErrorCode errorCode) {
        super(errorCode);
    }
}
