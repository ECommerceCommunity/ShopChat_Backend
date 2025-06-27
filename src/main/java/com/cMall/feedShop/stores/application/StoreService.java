package com.cMall.feedShop.stores.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.cMall.feedShop.stores.application.dto.request.StoreCreateRequest;
import com.cMall.feedShop.stores.application.dto.response.StoreResponse;
import com.cMall.feedShop.stores.domain.Store;
import com.cMall.feedShop.stores.domain.repository.StoreRepository;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreResponse createStore(StoreCreateRequest request) {
        Store store = new Store(
                request.getDescription(),
                request.getLogo(),
                request.getName()
        );

        Store saved = storeRepository.save(store);

        return new StoreResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getLogo(),
                saved.getNumbersLikes()
        );
    }
}
