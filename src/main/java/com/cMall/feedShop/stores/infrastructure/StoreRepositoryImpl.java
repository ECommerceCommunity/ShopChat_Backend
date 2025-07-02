package com.cMall.feedShop.stores.infrastructure;

import com.cMall.feedShop.stores.domain.Store;
import com.cMall.feedShop.stores.domain.repository.StoreRepositoryCustom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private final EntityManager em;

    @Override
    public Optional<Store> findStoreWithSpecialLogic(Long id) {
        return Optional.ofNullable(em.find(Store.class, id));
    }
}
