package com.cMall.feedShop.stores.domain.repository;

import com.cMall.feedShop.stores.domain.Store;
import java.util.Optional;

public interface StoreRepositoryCustom {
    Optional<Store> findStoreWithSpecialLogic(Long id); // 예시
}
