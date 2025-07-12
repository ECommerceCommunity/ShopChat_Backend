package com.cMall.feedShop.event.application.service;

import com.cMall.feedShop.event.application.dto.request.EventListRequestDto;
import com.cMall.feedShop.event.application.dto.response.EventListResponseDto;
import com.cMall.feedShop.event.domain.repository.EventRepository;
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
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        // 테스트 설정
    }

    @Test
    void searchEvents_ShouldReturnEventList() {
        // Given
        EventListRequestDto requestDto = EventListRequestDto.builder()
                .page(1)
                .size(10)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<com.cMall.feedShop.event.domain.Event> mockPage = new PageImpl<>(List.of(), pageable, 0);
        
        when(eventRepository.searchEvents(requestDto, pageable)).thenReturn(mockPage);

        // When
        EventListResponseDto response = eventService.searchEvents(requestDto);

        // Then
        assertNotNull(response);
        verify(eventRepository, times(1)).searchEvents(requestDto, pageable);
    }

    @Test
    void getAllEvents_ShouldReturnEventList() {
        // Given
        Integer page = 1;
        Integer size = 10;
        Pageable pageable = PageRequest.of(0, 10);
        Page<com.cMall.feedShop.event.domain.Event> mockPage = new PageImpl<>(List.of(), pageable, 0);
        
        when(eventRepository.findAll(pageable)).thenReturn(mockPage);

        // When
        EventListResponseDto response = eventService.getAllEvents(page, size);

        // Then
        assertNotNull(response);
        verify(eventRepository, times(1)).findAll(pageable);
    }
} 