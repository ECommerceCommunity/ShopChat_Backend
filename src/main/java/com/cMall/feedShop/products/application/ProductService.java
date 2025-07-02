package com.cMall.feedShop.products.application;

import com.cMall.feedShop.products.domain.Color;
import com.cMall.feedShop.products.domain.ProductColor;
import com.cMall.feedShop.products.domain.ProductSize;
import com.cMall.feedShop.products.domain.repository.ColorRepository;
import com.cMall.feedShop.products.domain.Product;
import com.cMall.feedShop.products.domain.repository.ProductRepository;
import com.cMall.feedShop.products.domain.repository.ProductOtherColorRepository;
import com.cMall.feedShop.products.application.dto.request.ProductCreateRequest;
import com.cMall.feedShop.products.application.dto.response.ProductResponse;
import com.cMall.feedShop.products.application.dto.response.OtherColorProductDto;
import com.cMall.feedShop.products.domain.ProductMainImageUrl;
import com.cMall.feedShop.products.domain.ProductDetailImageUrl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        for (String colorName : req.getColorNames()) {
            Color color = colorRepository.findByName(colorName)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 색상명: " + colorName));
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

    @Transactional
    public Long registerProduct(ProductCreateRequest req, MultipartFile mainImage, MultipartFile detailImage) {
        try {
            // 1. 이미지 업로드 (로컬 예시)
            String uploadDir = "uploads/";

            String mainImageFileName = System.currentTimeMillis() + "_" + mainImage.getOriginalFilename();
            Path mainImagePath = Paths.get(uploadDir + mainImageFileName);
            Files.write(mainImagePath, mainImage.getBytes());

            String detailImageFileName = System.currentTimeMillis() + "_" + detailImage.getOriginalFilename();
            Path detailImagePath = Paths.get(uploadDir + detailImageFileName);
            Files.write(detailImagePath, detailImage.getBytes());

            // 2. Product 생성
            Product product = new Product(
                    req.getName(),
                    req.getPrice(),
                    req.getGender(),
                    req.getDescription(),
                    req.getModelCode()
            );

            // 3. 대표 이미지 Entity 생성 후 Product에 추가
            ProductMainImageUrl mainImageEntity = new ProductMainImageUrl(mainImagePath.toString(), product);
            product.addMainImageUrl(mainImageEntity);

            // 4. 상세 이미지 Entity 생성 후 Product에 추가
            ProductDetailImageUrl detailImageEntity = new ProductDetailImageUrl(detailImagePath.toString(), product);
            product.addDetailImageUrl(detailImageEntity);

            // 5. 색상 추가 (colorIds → Color Entity 조회 → ProductColor 생성)
            for (String colorName : req.getColorNames()) {
                Color color = colorRepository.findByName(colorName)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 색상명: " + colorName));
                ProductColor productColor = new ProductColor(product, color);
                product.addProductColor(productColor);
            }
            
            // 6. 옵션 추가 (사이즈, 재고, 종류)
            for (ProductCreateRequest.OptionDto optionDto : req.getOptions()) {
                ProductSize option = new ProductSize(
                        optionDto.getSize(),
                        optionDto.getStock(),
                        product,
                        optionDto.getType() // 종류 (운동화, 부츠 등)
                );
                product.addProductSize(option);
            }

            // 7. 저장
            productRepository.save(product);

            return product.getId();

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }
}