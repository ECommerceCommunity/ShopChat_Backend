package products_stores.domain.dto.BrandDTO;

import products_stores.domain.model.Brand;

public class BrandResponseDto {
    private Long id;
    private String name;
    private String logoUrl;

    public BrandResponseDto(Brand brand) {
        this.id = brand.getId();
        this.name = brand.getName();
        this.logoUrl = brand.getLogoUrl();
    }

    // --- Getters ---
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
