package com.cMall.feedShop.event.domain.repository;

import com.cMall.feedShop.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventJpaRepository extends JpaRepository<Event, Long> {
    // Spring Data JPA 기본 메서드 사용
    
    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL")
    java.util.List<Event> findAllActive();

    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL")
    Page<Event> findAllActive(Pageable pageable);
} 