package com.cMall.feedShop.stores.domain.repository;

import com.cMall.feedShop.stores.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
