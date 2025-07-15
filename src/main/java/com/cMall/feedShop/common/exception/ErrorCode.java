package com.cMall.feedShop.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 공통
    INVALID_INPUT_VALUE(400, "C001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(405, "C002", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(500, "C003", "서버 오류가 발생했습니다."),
    DATA_INTEGRITY_VIOLATION(409, "C004", "데이터 무결성 제약 조건을 위반했습니다."),

    // 인증/인가
    UNAUTHORIZED(401, "A001", "인증이 필요합니다."),
    FORBIDDEN(403, "A002", "권한이 없습니다."),

    // 사용자
    USER_NOT_FOUND(404, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(409, "U002", "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(400, "U003", "비밀번호가 일치하지 않습니다."),
    USER_ALREADY_DELETED(409, "U004", "이미 탈퇴 처리된 계정입니다."),
    INVALID_VERIFICATION_TOKEN(400, "U005", "유효하지 않거나 찾을 수 없는 인증 토큰입니다."),
    ACCOUNT_ALREADY_VERIFIED(409, "U006", "이미 인증이 완료된 계정입니다."),
    VERIFICATION_TOKEN_EXPIRED(400, "U007", "인증 토큰이 만료되었습니다. 다시 회원가입을 시도하거나 인증 메일을 재발송해주세요."),

    // 스토어
    STORE_FORBIDDEN(403, "S001", "해당 스토어의 관리자가 아닙니다."),
    STORE_NOT_FOUND(404, "S002", "스토어를 찾을 수 없습니다."),

    // 상품
    PRODUCT_NOT_FOUND(404, "P001", "상품을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(404, "P002", "카테고리를 찾을 수 없습니다."),
    OUT_OF_STOCK(409, "P003", "재고가 부족합니다."),
    PRODUCT_IN_ORDER(409, "P004", "상품이 주문에 포함되어 있어 삭제할 수 없습니다."),

    // 주문
    ORDER_NOT_FOUND(404, "O001", "주문을 찾을 수 없습니다."),
    INVALID_ORDER_STATUS(400, "O002", "잘못된 주문 상태입니다.");

    private final int status;
    private final String code;
    private final String message;
}