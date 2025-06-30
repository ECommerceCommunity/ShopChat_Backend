package com.cMall.feedShop.stores.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.cMall.feedShop.stores.application.dto.request.StoreCreateRequest;
import com.cMall.feedShop.stores.application.dto.response.StoreResponse;
import com.cMall.feedShop.stores.application.StoreService;

@RestController
@RequiredArgsConstructor
public class StoreController implements StoreApi {

    private final StoreService storeService;

    @Override
    public ResponseEntity<StoreResponse> createStore(StoreCreateRequest request) {
        StoreResponse response = storeService.createStore(request);
        return ResponseEntity.ok(response);
    }
}
