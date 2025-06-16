@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponseDto>> createBrand(
            @RequestBody @Valid BrandRequestDto dto) {
        BrandResponseDto result = brandService.createBrand(dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponseDto>>> getAllBrands() {
        return ResponseEntity.ok(ApiResponse.success(brandService.getAllBrands()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponseDto>> getBrand(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(brandService.getBrandById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponseDto>> updateBrand(
            @PathVariable Long id, @RequestBody @Valid BrandRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(brandService.updateBrand(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
