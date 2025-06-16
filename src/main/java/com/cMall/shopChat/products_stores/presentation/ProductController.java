@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ProductResponseDto>> registerProduct(
            @RequestBody @Valid ProductRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(productService.registerProduct(dto)));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getAllProducts() {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(productService.updateProduct(id, dto)));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
