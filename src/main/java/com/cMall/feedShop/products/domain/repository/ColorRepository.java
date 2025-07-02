package com.cMall.feedShop.products.domain.repository;

import com.cMall.feedShop.products.domain.Color;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Long> {

    Optional<Color> findByName(String name);
}
