package com.cMall.feedShop.product.application.exception;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;

public class ProductException extends BusinessException {
    public ProductException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ProductException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static class ProductNotFoundException extends ProductException {
        public ProductNotFoundException() {
            super(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    public static class CategoryNotFoundException extends ProductException {
        public CategoryNotFoundException() {
            super(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    public static class OutOfStockException extends ProductException {
        public OutOfStockException() {
            super(ErrorCode.OUT_OF_STOCK);
        }
    }
}
