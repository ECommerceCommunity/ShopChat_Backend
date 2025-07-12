package com.cMall.feedShop.event.infrastructure;

import com.cMall.feedShop.event.domain.Event;
import com.cMall.feedShop.event.domain.repository.EventJpaRepository;
import com.cMall.feedShop.event.domain.repository.EventQueryRepository;
import com.cMall.feedShop.event.domain.repository.EventRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventRepositoryImplTest {

    @Mock
    private EventJpaRepository eventJpaRepository;

    @Mock
    private EventQueryRepository eventQueryRepository;

    @InjectMocks
    private EventRepositoryImpl eventRepositoryImpl;

    @BeforeEach
    void setUp() {
        // 테스트 설정
    }

    @Test
    void findAll_ShouldReturnEventPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Event> events = List.of();
        Page<Event> expectedPage = new PageImpl<>(events, pageable, 0);

        when(eventJpaRepository.findAll(pageable)).thenReturn(expectedPage);

        // When
        Page<Event> result = eventRepositoryImpl.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(expectedPage, result);
        verify(eventJpaRepository, times(1)).findAll(pageable);
    }
} 