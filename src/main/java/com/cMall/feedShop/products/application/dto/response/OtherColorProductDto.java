package com.cMall.feedShop.products.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtherColorProductDto {
    private Long id;
    private String name;
    private String thumbnailUrl;

    // (Long id, String thumbnailUrl) 생성자 추가
    public OtherColorProductDto(Long id, String thumbnailUrl) {
        this.id = id;
        this.thumbnailUrl = thumbnailUrl;
    }
}
