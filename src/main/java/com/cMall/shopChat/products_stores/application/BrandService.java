public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public BrandResponseDto createBrand(BrandRequestDto dto) {
        Brand brand = new Brand();
        brand.setName(dto.getName());
        brand.setLogoUrl(dto.getLogoUrl());
        return new BrandResponseDto(brandRepository.save(brand));
    }

    public List<BrandResponseDto> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(BrandResponseDto::new)
                .collect(Collectors.toList());
    }

    public BrandResponseDto getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", id));
        return new BrandResponseDto(brand);
    }

    public BrandResponseDto updateBrand(Long id, BrandRequestDto dto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", id));
        brand.setName(dto.getName());
        brand.setLogoUrl(dto.getLogoUrl());
        return new BrandResponseDto(brandRepository.save(brand));
    }

    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", id));
        brandRepository.delete(brand);
    }
}
