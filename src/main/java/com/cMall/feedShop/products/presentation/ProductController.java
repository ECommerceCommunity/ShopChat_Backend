package com.cMall.feedShop.products.presentation;

import com.cMall.feedShop.products.application.ProductService;
import com.cMall.feedShop.products.application.dto.response.ProductResponse;
import com.cMall.feedShop.products.application.dto.response.OtherColorProductDto;
import com.cMall.feedShop.products.domain.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        Product product = productService.findById(id); // Product 반환

        // product에서 modelCode 가져오기
        String modelCode = product.getModelCode();

        // modelCode와 id 모두 전달
        List<OtherColorProductDto> otherColors = productService.findOtherColorProductsByModelCode(modelCode, id);

        return ProductResponse.from(product, otherColors);
    }

}