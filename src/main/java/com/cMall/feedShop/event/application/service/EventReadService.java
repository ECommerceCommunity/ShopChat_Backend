package com.cMall.feedShop.event.application.service;

import com.cMall.feedShop.event.application.dto.request.EventListRequestDto;
import com.cMall.feedShop.event.application.dto.response.EventListResponseDto;
import com.cMall.feedShop.event.application.dto.response.EventSummaryDto;
import com.cMall.feedShop.event.domain.Event;
import com.cMall.feedShop.event.domain.repository.EventRepository;
import com.cMall.feedShop.event.application.dto.response.FeedEventDto;
import com.cMall.feedShop.event.domain.enums.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventReadService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    /**
     * 전체 이벤트 목록 조회 (페이징)
     */
    public EventListResponseDto getAllEvents(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page != null ? page - 1 : 0, size != null ? size : 20);
        Page<Event> eventPage = eventRepository.findAll(pageable);
        List<EventSummaryDto> content = eventPage.getContent().stream()
                .map(eventMapper::toSummaryDto)
                .toList();
        return EventListResponseDto.builder()
                .content(content)
                .page(pageable.getPageNumber() + 1)
                .size(pageable.getPageSize())
                .totalElements(eventPage.getTotalElements())
                .totalPages(eventPage.getTotalPages())
                .build();
    }

    /**
     * 검색 (조건 기반, QueryDSL)
     */
    public EventListResponseDto searchEvents(EventListRequestDto requestDto) {
        int page = requestDto.getPage() != null ? requestDto.getPage() - 1 : 0;
        int size = requestDto.getSize() != null ? requestDto.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.searchEvents(requestDto, pageable);
        List<EventSummaryDto> content = eventPage.getContent().stream()
                .map(eventMapper::toSummaryDto)
                .toList();
        return EventListResponseDto.builder()
                .content(content)
                .page(page + 1)
                .size(size)
                .totalElements(eventPage.getTotalElements())
                .totalPages(eventPage.getTotalPages())
                .build();
    }

    /**
     * 피드 생성용 이벤트 목록 조회 (진행중인 이벤트만)
     */
    public List<FeedEventDto> getFeedAvailableEvents() {
        EventListRequestDto requestDto = EventListRequestDto.builder()
                .status("ONGOING")  // 진행중인 이벤트만
                .build();
        
        Pageable pageable = PageRequest.of(0, 100); // 충분한 수량 조회
        Page<Event> eventPage = eventRepository.searchEvents(requestDto, pageable);
        
        return eventPage.getContent().stream()
                .map(this::toFeedEventDto)
                .toList();
    }
    
    /**
     * Event를 FeedEventDto로 변환
     */
    private FeedEventDto toFeedEventDto(Event event) {
        return FeedEventDto.builder()
                .eventId(event.getId())
                .title(event.getEventDetail().getTitle())
                .eventStartDate(event.getEventDetail().getEventStartDate().toString())
                .eventEndDate(event.getEventDetail().getEventEndDate().toString())
                .type(event.getType().name())
                .build();
    }
} 