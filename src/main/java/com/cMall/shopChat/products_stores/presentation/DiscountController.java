package products_stores.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import products_stores.application.DiscountService;
import products_stores.common.response.ApiResponse;
import products_stores.domain.dto.DiscountDTO.DiscountRequestDto;
import products_stores.domain.dto.DiscountDTO.DiscountResponseDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DiscountResponseDto>> createDiscount(
            @RequestBody @Valid DiscountRequestDto dto) {
        DiscountResponseDto result = discountService.createDiscount(dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DiscountResponseDto>>> getAllDiscounts() {
        return ResponseEntity.ok(ApiResponse.success(discountService.getAllDiscounts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DiscountResponseDto>> getDiscount(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(discountService.getDiscountById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DiscountResponseDto>> updateDiscount(
            @PathVariable Long id,
            @RequestBody @Valid DiscountRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(discountService.updateDiscount(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
