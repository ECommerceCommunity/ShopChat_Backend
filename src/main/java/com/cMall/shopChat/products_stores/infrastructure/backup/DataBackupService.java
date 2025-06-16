package com.cMall.shopChat.products_stores.infrastructure.backup;

import com.cMall.shopChat.products_stores.domain.model.Product;
import com.cMall.shopChat.products_stores.domain.model.Brand;
import com.cMall.shopChat.products_stores.domain.model.Discount;
import com.cMall.shopChat.products_stores.domain.repository.ProductRepository;
import com.cMall.shopChat.products_stores.domain.repository.BrandRepository;
import com.cMall.shopChat.products_stores.domain.repository.DiscountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class DataBackupService {

    private final ProductRepository productRepo;
    private final BrandRepository brandRepo;
    private final DiscountRepository discountRepo;
    private final ObjectMapper objectMapper;

    public DataBackupService(ProductRepository productRepo,
                             BrandRepository brandRepo,
                             DiscountRepository discountRepo) {
        this.productRepo = productRepo;
        this.brandRepo = brandRepo;
        this.discountRepo = discountRepo;
        this.objectMapper = new ObjectMapper();
    }

    public void backupAllToJson() {
        try {
            List<Product> products = productRepo.findAll();
            List<Brand> brands = brandRepo.findAll();
            List<Discount> discounts = discountRepo.findAll();

            objectMapper.writeValue(new File("products_backup.json"), products);
            objectMapper.writeValue(new File("brands_backup.json"), brands);
            objectMapper.writeValue(new File("discounts_backup.json"), discounts);

            System.out.println("✅ 백업이 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
