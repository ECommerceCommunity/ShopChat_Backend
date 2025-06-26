package com.cMall.feedShop.products.application;

import com.cMall.feedShop.products.domain.Color;
import com.cMall.feedShop.products.domain.ProductColor;
import com.cMall.feedShop.products.domain.ProductSize;
import com.cMall.feedShop.products.domain.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cMall.feedShop.products.application.dto.request.ProductCreateRequest;
import com.cMall.feedShop.products.application.dto.response.ProductResponse;
import com.cMall.feedShop.products.domain.Product;
import com.cMall.feedShop.products.domain.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;

    @Transactional
    public Long create(ProductCreateRequest req) {
        Product product = new Product(
                req.getName(),
                req.getShoesType(),
                req.getPrice(),
                req.getGender(),
                req.getMainImageUrls(),
                req.getDetailImageUrls()
        );

        // 색상 추가
        for (Long colorId : req.getColorIds()) {
            Color color = colorRepository.findById(colorId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 색상입니다."));
            ProductColor productColor = new ProductColor(product, color);
            product.getProductColors().add(productColor);
        }

        // 옵션(사이즈/재고) 추가
        for (ProductCreateRequest.OptionDto optionDto : req.getOptions()) {
            ProductSize option = new ProductSize(optionDto.getSize(), optionDto.getStock(), product);
            product.getProductSizes().add(option);
        }

        productRepository.save(product); // 연관된 option과 color도 함께 저장됨

        return product.getId();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }
}
