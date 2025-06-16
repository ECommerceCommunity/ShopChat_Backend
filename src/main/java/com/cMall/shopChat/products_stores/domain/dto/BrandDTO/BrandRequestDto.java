package products_stores.domain.dto.BrandDTO;

import javax.validation.constraints.NotBlank;

public class BrandRequestDto {

    @NotBlank(message = "브랜드 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "로고 URL은 필수입니다.")
    private String logoUrl;

    // --- Getters ---
    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    // --- Setters ---
    public void setName(String name) {
        this.name = name;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
