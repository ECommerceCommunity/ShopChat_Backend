package com.cMall.feedShop.products.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cMall.feedShop.products.application.ProductService;
import com.cMall.feedShop.products.application.dto.request.ProductCreateRequest;
import com.cMall.feedShop.products.application.dto.response.ProductResponse;

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
}