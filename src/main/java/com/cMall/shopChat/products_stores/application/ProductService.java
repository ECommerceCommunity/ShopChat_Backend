@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDto registerProduct(ProductRequestDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setShoesType(dto.setShoesType());
        product.setPrice(dto.getPrice());
        product.setSizes(dto.getSizes());
        product.setStock(dto.getStock());
        product.setDescription(dto.getDescription());
        return new ProductResponseDto(productRepository.save(product));
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
    }

    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return new ProductResponseDto(product);
    }

    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.setName(dto.getName());
        product.setShoesType(dto.getShoesType());
        product.setPrice(dto.getPrice());
        product.setSizes(dto.getSizes());
        product.setStock(dto.getStock());
        product.setDescription(dto.getDescription());
        return new ProductResponseDto(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        productRepository.delete(product);
    }
}
