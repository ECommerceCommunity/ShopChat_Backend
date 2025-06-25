package com.cMall.feedShop.products.application;

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

    @Transactional
    public Long create(ProductCreateRequest req) {
        Product product = new Product(
                req.getStoreId(),
                req.getUserId(),
                req.getName(),
                req.getShoesType(),
                req.getPrice(),
                req.getSizeStockList(),
                req.getGender(),
                req.getMainImageUrls(),
                req.getDetailImageUrls(),
                req.getColorInfo()
        );
        return productRepository.save(product).getId();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }
}
