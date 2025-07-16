// 이벤트 관련 비즈니스 로직을 담당하는 서비스 클래스
package com.cMall.feedShop.event.application.service;

import com.cMall.feedShop.event.application.dto.request.EventListRequestDto;
import com.cMall.feedShop.event.application.dto.response.EventListResponseDto;
import com.cMall.feedShop.event.application.dto.response.EventSummaryDto;
import com.cMall.feedShop.event.domain.Event;
import com.cMall.feedShop.event.domain.EventDetail;
import com.cMall.feedShop.event.domain.EventReward;
import com.cMall.feedShop.event.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Collections;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    // 전체 조회 (페이징)
    public EventListResponseDto getAllEvents(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page != null ? page - 1 : 0, size != null ? size : 20);
        Page<Event> eventPage = eventRepository.findAll(pageable);
        List<EventSummaryDto> content = eventPage.getContent().stream()
                .map(this::toSummaryDto)
                .toList();
        return EventListResponseDto.builder()
                .content(content)
                .page(pageable.getPageNumber() + 1)
                .size(pageable.getPageSize())
                .totalElements(eventPage.getTotalElements())
                .totalPages(eventPage.getTotalPages())
                .build();
    }

    // 검색 (조건 기반, QueryDSL)
    public EventListResponseDto searchEvents(EventListRequestDto requestDto) {
        int page = requestDto.getPage() != null ? requestDto.getPage() - 1 : 0;
        int size = requestDto.getSize() != null ? requestDto.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.searchEvents(requestDto, pageable);
        List<EventSummaryDto> content = eventPage.getContent().stream()
                .map(this::toSummaryDto)
                .toList();
        return EventListResponseDto.builder()
                .content(content)
                .page(page + 1)
                .size(size)
                .totalElements(eventPage.getTotalElements())
                .totalPages(eventPage.getTotalPages())
                .build();
    }

    // Entity → DTO 변환 (간단 버전)
    private EventSummaryDto toSummaryDto(Event event) {
        EventDetail detail = event.getEventDetail();
        
        return EventSummaryDto.builder()
                .eventId(event.getId())
                .title(getSafeString(detail, EventDetail::getTitle))
                .type(getEventType(event))
                .status(getEventStatus(event))
                .eventStartDate(getSafeLocalDateString(detail, EventDetail::getEventStartDate))
                .eventEndDate(getSafeLocalDateString(detail, EventDetail::getEventEndDate))
                .imageUrl(getSafeString(detail, EventDetail::getImageUrl))
                .maxParticipants(event.getMaxParticipants())
                .description(getSafeString(detail, EventDetail::getDescription))
                .rewards(mapRewards(event))
                .participationMethod(getSafeString(detail, EventDetail::getParticipationMethod))
                .selectionCriteria(getSafeString(detail, EventDetail::getSelectionCriteria))
                .precautions(getSafeString(detail, EventDetail::getPrecautions))
                .createdBy("")
                .createdAt(event.getCreatedBy() != null ? event.getCreatedBy().toString() : null)
                .purchasePeriod(createPurchasePeriod(detail))
                .votePeriod(createVotePeriod(detail))
                .announcementDate(getSafeLocalDateString(detail, EventDetail::getAnnouncement))
                .build();
    }

    private List<EventSummaryDto.Reward> mapRewards(Event event) {
        return event.getRewards() != null ? event.getRewards().stream()
                .map(r -> EventSummaryDto.Reward.builder()
                        .rank(r.getConditionValue())
                        .reward(r.getRewardValue())
                        .build())
                .toList() : Collections.emptyList();
    }

    private String createPurchasePeriod(EventDetail detail) {
        return createDateRangeString(detail, EventDetail::getPurchaseStartDate, EventDetail::getPurchaseEndDate);
    }

    private String createVotePeriod(EventDetail detail) {
        return createDateRangeString(detail, EventDetail::getEventStartDate, EventDetail::getEventEndDate);
    }

    private String createDateRangeString(EventDetail detail, 
                                       Function<EventDetail, java.time.LocalDate> startGetter,
                                       Function<EventDetail, java.time.LocalDate> endGetter) {
        if (detail == null) return "";
        
        var startDate = startGetter.apply(detail);
        var endDate = endGetter.apply(detail);
        
        return (startDate != null && endDate != null) 
                ? startDate + " ~ " + endDate 
                : "";
    }

    private String getSafeString(EventDetail detail, Function<EventDetail, String> getter) {
        return detail != null ? getter.apply(detail) : "";
    }

    private String getSafeLocalDateString(EventDetail detail, Function<EventDetail, java.time.LocalDate> getter) {
        if (detail == null) return "";
        var date = getter.apply(detail);
        return date != null ? date.toString() : "";
    }

    private String getEventType(Event event) {
        return event.getType() != null ? event.getType().name().toLowerCase() : null;
    }

    private String getEventStatus(Event event) {
        return event.getStatus() != null ? event.getStatus().name().toLowerCase() : null;
    }
} 