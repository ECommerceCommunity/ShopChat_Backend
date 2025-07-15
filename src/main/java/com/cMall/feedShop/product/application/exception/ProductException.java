package com.cMall.feedShop.product.application.exception;

import com.cMall.feedShop.common.exception.BusinessException;
import com.cMall.feedShop.common.exception.ErrorCode;

public class ProductException {
    public static class ProductNotFoundException extends BusinessException {
        public ProductNotFoundException() {
            super(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    public static class CategoryNotFoundException extends BusinessException {
        public CategoryNotFoundException() {
            super(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    public static class OutOfStockException extends BusinessException {
        public OutOfStockException() {
            super(ErrorCode.OUT_OF_STOCK);
        }
    }

    public static class ProductInOrderException extends BusinessException {
        public ProductInOrderException() { super(ErrorCode.PRODUCT_IN_ORDER); }
    }
}
