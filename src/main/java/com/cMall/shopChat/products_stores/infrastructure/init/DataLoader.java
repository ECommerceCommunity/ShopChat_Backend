package com.cMall.shopChat.products_stores.infrastructure.init;

import com.cMall.shopChat.products_stores.domain.model.Product;
import com.cMall.shopChat.products_stores.domain.model.Brand;
import com.cMall.shopChat.products_stores.domain.model.Discount;
import com.cMall.shopChat.products_stores.domain.repository.ProductRepository;
import com.cMall.shopChat.products_stores.domain.repository.BrandRepository;
import com.cMall.shopChat.products_stores.domain.repository.DiscountRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class DataLoader {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final DiscountRepository discountRepository;
    private final ObjectMapper objectMapper;

    public DataLoader(ProductRepository productRepository,
                      BrandRepository brandRepository,
                      DiscountRepository discountRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.discountRepository = discountRepository;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        loadProducts();
        loadBrands();
        loadDiscounts();
    }

    private void loadProducts() {
        try (InputStream is = getClass().getResourceAsStream("/static/products.json")) {
            List<Map<String, Object>> products = objectMapper.readValue(is, new TypeReference<>() {});
            for (Map<String, Object> map : products) {
                Product p = new Product();
                p.setProductId(Long.parseLong(map.get("product_id").toString()));
                p.setName((String) map.get("name"));
                p.setShoesType((String) map.get("shoesType"));
                p.setGender((String) map.get("gender"));
                p.setPrice((String) map.get("price"));
                p.setSizeStockList((String) map.get("size_stock_list"));
                p.setMainImageUrls((String) map.get("main_image_urls"));
                p.setDetailImageUrls((String) map.get("detail_image_urls"));
                p.setStoreId((String) map.get("store_id"));
                p.setCouponApplicable((String) map.get("쿠폰 적용 가능 여부"));
                p.setCouponDiscountRate((Integer) map.get("쿠폰 할인율(%)"));
                p.setPointApplicable((String) map.get("포인트 적립 가능 여부"));
                p.setPointRate((Integer) map.get("적립 포인트 비율(%)"));
                p.setMaxPoint((Integer) map.get("최대 적립 포인트"));
                p.setProductLikes((Integer) map.get("product_likes"));
                productRepository.save(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBrands() {
        try (InputStream is = getClass().getResourceAsStream("/static/brands.json")) {
            List<Map<String, Object>> brands = objectMapper.readValue(is, new TypeReference<>() {});
            for (Map<String, Object> map : brands) {
                Brand b = new Brand();
                b.setStoreId((String) map.get("store_id"));
                b.setBrandInfo((String) map.get("brand_info"));
                b.setBrandLogoUrl((String) map.get("brand_logo_url"));
                b.setBrandLikes((Integer) map.get("brand_likes"));
                brandRepository.save(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDiscounts() {
        try (InputStream is = getClass().getResourceAsStream("/static/discounts.json")) {
            List<Map<String, Object>> discounts = objectMapper.readValue(is, new TypeReference<>() {});
            for (Map<String, Object> map : discounts) {
                Discount d = new Discount();
                d.setProductId(Long.parseLong(map.get("product_id").toString()));
                d.setDiscountType((String) map.get("discount_type"));
                d.setDiscountValue(Double.valueOf(map.get("discount_value").toString()));
                d.setDiscountStart(LocalDate.parse((String) map.get("discount_start")));
                d.setDiscountEnd(LocalDate.parse((String) map.get("discount_end")));
                discountRepository.save(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
