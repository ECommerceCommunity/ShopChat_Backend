package com.cMall.feedShop.stores.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cMall.feedShop.stores.application.dto.request.StoreCreateRequest;
import com.cMall.feedShop.stores.application.dto.response.StoreResponse;

@RequestMapping("/api/stores")
public interface StoreApi {

    @PostMapping
    ResponseEntity<StoreResponse> createStore(@RequestBody StoreCreateRequest request);
}
