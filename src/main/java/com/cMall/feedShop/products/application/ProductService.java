package com.cMall.feedShop.products.application;

import com.cMall.feedShop.products.domain.Color;
import com.cMall.feedShop.products.domain.ProductColor;
import com.cMall.feedShop.products.domain.ProductSize;
import com.cMall.feedShop.products.domain.repository.ColorRepository;
import com.cMall.feedShop.products.domain.Product;
import com.cMall.feedShop.products.domain.ProductOtherColor;
import com.cMall.feedShop.products.domain.repository.ProductRepository;
import com.cMall.feedShop.products.domain.repository.ProductOtherColorRepository;
import com.cMall.feedShop.products.application.dto.request.ProductCreateRequest;
import com.cMall.feedShop.products.application.dto.response.ProductResponse;
import com.cMall.feedShop.products.application.dto.response.OtherColorProductDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final ProductOtherColorRepository productOtherColorRepository;

    @Transactional
    public Long create(ProductCreateRequest req) {
        Product product = new Product(
                req.getName(),
                req.getPrice(),
                req.getGender(),
                req.getDescription(),
                req.getModelCode()
        );

        // 색상 추가
        for (Long colorId : req.getColorIds()) {
            Color color = colorRepository.findById(colorId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 색상입니다."));
            ProductColor productColor = new ProductColor(product, color);
            product.addProductColor(productColor);
        }

        // 옵션(사이즈/재고) 추가
        for (ProductCreateRequest.OptionDto optionDto : req.getOptions()) {
            ProductSize option = new ProductSize(
                    optionDto.getSize(),
                    optionDto.getStock(),
                    product,
                    optionDto.getType()
            );
            product.addProductSize(option);
        }

        productRepository.save(product); // 연관된 option과 color도 함께 저장됨

        return product.getId();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        List<Product> products = productRepository.findAllWithDetails();
        System.out.println("조회된 상품 수: " + products.size());

        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        Product product = productRepository.findByIdWithDetails(id);
        if (product == null) {
            throw new IllegalArgumentException("해당 ID의 상품이 존재하지 않습니다: " + id);
        }
        return product;
    }

    @Transactional(readOnly = true)
    public List<OtherColorProductDto> findOtherColorProductsByModelCode(String modelCode, Long currentProductId) {
        return productRepository.findOtherColorProductsByModelCode(modelCode, currentProductId);
    }

    @Transactional(readOnly = true)
    public List<OtherColorProductDto> findOtherColorProductsByProductId(Long productId) {
        return productOtherColorRepository.findOtherColorsByProductId(productId);
    }
}