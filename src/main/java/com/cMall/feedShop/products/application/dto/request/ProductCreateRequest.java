package com.cMall.feedShop.products.application.dto.request;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class ProductCreateRequest {

    private String name;                   // 상품명
    private BigDecimal price;              // 가격
    private String gender;                 // 성별 (남성/여성/공용)
    private String description;            // 상품 설명
    private String modelCode;              // 모델 코드

    private List<String> colorNames;       // 색상명 리스트

    private List<String> mainImageUrls;    // 대표 이미지 URL 리스트 (파일 업로드 후 URL 저장 시 사용)
    private List<String> detailImageUrls;  // 상세 이미지 URL 리스트

    private List<OptionDto> options;       // 옵션 리스트

    @Getter
    public static class OptionDto {
        private String size;               // 사이즈
        private Integer stock;             // 재고
        private String type;               // 종류 (운동화, 부츠 등)
    }
}
