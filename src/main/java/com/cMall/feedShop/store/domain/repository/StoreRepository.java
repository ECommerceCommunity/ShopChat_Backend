package com.cMall.feedShop.store.domain.repository;

import com.cMall.feedShop.store.domain.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
