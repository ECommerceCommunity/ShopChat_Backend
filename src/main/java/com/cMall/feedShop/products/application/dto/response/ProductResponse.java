package com.cMall.feedShop.products.application.dto.response;

import com.cMall.feedShop.products.domain.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.cMall.feedShop.products.application.dto.response.OtherColorProductDto;

@Getter
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String gender;
    private String description;
    private Integer numbersLikes;
    private List<ColorDto> colors;
    private List<SizeDto> sizes;
    private List<DiscountDto> discounts;
    private List<String> mainImageUrls;
    private List<String> detailImageUrls;
    private StoreDto store;
    private List<OtherColorProductDto> otherColorProducts;

    public static ProductResponse from(Product product) {
        return from(product, List.of());
    }

    public static ProductResponse from(Product product, List<OtherColorProductDto> otherColors) {
        List<ColorDto> colors = Optional.ofNullable(product.getProductColors())
                .orElse(List.of())
                .stream()
                .map(pc -> new ColorDto(
                        pc.getColor().getId(),
                        pc.getColor().getName(),
                        pc.getColor().getEnglishName(),
                        pc.getColor().getImageUrl()
                ))
                .collect(Collectors.toList());

        List<SizeDto> sizes = Optional.ofNullable(product.getProductSizes())
                .orElse(List.of())
                .stream()
                .map(ps -> new SizeDto(
                        ps.getId(),
                        ps.getSize(),
                        ps.getStock(),
                        ps.getType()
                ))
                .collect(Collectors.toList());

        List<DiscountDto> discounts = Optional.ofNullable(product.getProductDiscounts())
                .orElse(List.of())
                .stream()
                .map(pd -> new DiscountDto(
                        pd.getId(),
                        pd.getDiscountPrice() != null ? pd.getDiscountPrice().getDiscountType() : null,
                        pd.getDiscountPrice() != null ? pd.getDiscountPrice().getDiscountValue() : null,
                        pd.getStartDate() != null ? pd.getStartDate().toString() : null,
                        pd.getEndDate() != null ? pd.getEndDate().toString() : null
                ))
                .collect(Collectors.toList());

        List<String> mainImageUrls = Optional.ofNullable(product.getMainImageUrls())
                .orElse(List.of())
                .stream()
                .map(ProductMainImageUrl::getImageUrl)
                .collect(Collectors.toList());

        List<String> detailImageUrls = Optional.ofNullable(product.getDetailImageUrls())
                .orElse(List.of())
                .stream()
                .map(ProductDetailImageUrl::getImageUrl)
                .collect(Collectors.toList());

        StoreDto store = null;
        if (product.getStore() != null) {
            store = new StoreDto(
                    product.getStore().getId(),
                    product.getStore().getName(),
                    product.getStore().getDescription()
            );
        }

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getGender(),
                product.getDescription(),
                product.getNumbersLikes(),
                colors,
                sizes,
                discounts,
                mainImageUrls,
                detailImageUrls,
                store,
                otherColors
        );
    }

    @Getter
    @AllArgsConstructor
    public static class ColorDto {
        private Long id;
        private String name;
        private String englishName;
        private String imageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class SizeDto {
        private Long id;
        private String size;
        private Integer stock;
        private String type;
    }

    @Getter
    @AllArgsConstructor
    public static class DiscountDto {
        private Long id;
        private String discountType;
        private BigDecimal discountValue;
        private String startDate;
        private String endDate;
    }

    @Getter
    @AllArgsConstructor
    public static class StoreDto {
        private Long id;
        private String name;
        private String description;
    }
}