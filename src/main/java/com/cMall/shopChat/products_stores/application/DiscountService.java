@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public DiscountResponseDto createDiscount(DiscountRequestDto dto) {
        Discount discount = new Discount();
        discount.setProductId(dto.getProductId());
        discount.setDiscountType(dto.getDiscountType());
        discount.setAmount(dto.getAmount());
        discount.setStartDate(dto.getStartDate());
        discount.setEndDate(dto.getEndDate());
        return new DiscountResponseDto(discountRepository.save(discount));
    }

    public List<DiscountResponseDto> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(DiscountResponseDto::new)
                .collect(Collectors.toList());
    }

    public DiscountResponseDto getDiscountById(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", id));
        return new DiscountResponseDto(discount);
    }

    public DiscountResponseDto updateDiscount(Long id, DiscountRequestDto dto) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", id));
        discount.setDiscountType(dto.getDiscountType());
        discount.setAmount(dto.getAmount());
        discount.setStartDate(dto.getStartDate());
        discount.setEndDate(dto.getEndDate());
        return new DiscountResponseDto(discountRepository.save(discount));
    }

    public void deleteDiscount(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", id));
        discountRepository.delete(discount);
    }
}
