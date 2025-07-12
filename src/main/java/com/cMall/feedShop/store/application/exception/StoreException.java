package com.cMall.feedShop.store.application.exception;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;

public class StoreException extends BusinessException {
    public StoreException(ErrorCode errorCode) {
        super(errorCode);
    }

    public StoreException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static class StoreForbiddenException extends StoreException {
        public StoreForbiddenException() {
            super(ErrorCode.STORE_FORBIDDEN);
        }
    }

    public static class StoreNotFoundException extends StoreException {
        public StoreNotFoundException() {
            super(ErrorCode.STORE_NOT_FOUND);
        }
    }
}
