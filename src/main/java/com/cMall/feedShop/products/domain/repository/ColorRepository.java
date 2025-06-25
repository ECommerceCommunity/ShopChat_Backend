package com.cMall.feedShop.products.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cMall.feedShop.products.domain.Color;

public interface ColorRepository extends JpaRepository<Color, Long> {
}
