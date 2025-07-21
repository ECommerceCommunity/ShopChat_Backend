package com.cMall.feedShop.cart.presentation;

import com.cMall.feedShop.cart.application.dto.request.CartItemCreateRequest;
import com.cMall.feedShop.cart.application.dto.response.CartItemResponse;
import com.cMall.feedShop.cart.application.service.CartService;
import com.cMall.feedShop.common.aop.ApiResponseFormat;
import com.cMall.feedShop.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class CartUserController {

    private final CartService cartService;

    /**
     * 장바구니에 상품 추가
     * POST /api/users/cart/items
     */
    @PostMapping("/cart/items")
    @ApiResponseFormat(message = "상품이 장바구니에 추가되었습니다.")
    public ApiResponse<CartItemResponse> addCartItem(
            @Valid @RequestBody CartItemCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CartItemResponse data = cartService.addCartItem(request, userDetails);
        return ApiResponse.success(data);
    }
}
