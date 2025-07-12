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
import java.util.stream.Collectors;
import java.util.Collections;

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
                .collect(Collectors.toList());
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
                .collect(Collectors.toList());
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
        // 등수별 보상 매핑
        List<EventSummaryDto.Reward> rewards = event.getRewards() != null ? event.getRewards().stream()
                .map(r -> EventSummaryDto.Reward.builder()
                        .rank(r.getConditionValue())
                        .reward(r.getRewardValue())
                        .build())
                .collect(Collectors.toList()) : Collections.emptyList();
        // purchasePeriod, votePeriod, announcementDate 가공
        String purchasePeriod = (detail != null && detail.getPurchaseStartDate() != null && detail.getPurchaseEndDate() != null)
                ? detail.getPurchaseStartDate() + " ~ " + detail.getPurchaseEndDate()
                : "";
        String votePeriod = (detail != null && detail.getEventStartDate() != null && detail.getEventEndDate() != null)
                ? detail.getEventStartDate() + " ~ " + detail.getEventEndDate()
                : "";
        String announcementDate = (detail != null && detail.getAnnouncement() != null)
                ? detail.getAnnouncement().toString()
                : "";
        return EventSummaryDto.builder()
                .eventId(event.getId()) // 프론트 id 매핑
                .title(detail != null ? detail.getTitle() : "")
                .type(event.getType() != null ? event.getType().name().toLowerCase() : null)
                .status(event.getStatus() != null ? event.getStatus().name().toLowerCase() : null)
                .eventStartDate(detail != null && detail.getEventStartDate() != null ? detail.getEventStartDate().toString() : "")
                .eventEndDate(detail != null && detail.getEventEndDate() != null ? detail.getEventEndDate().toString() : "")
                .imageUrl(detail != null ? detail.getImageUrl() : "")
                .maxParticipants(event.getMaxParticipants()) // 프론트 participantCount 매핑
                .description(detail != null ? detail.getDescription() : "")
                .rewards(rewards)
                .participationMethod(detail != null ? detail.getParticipationMethod() : "")
                .selectionCriteria(detail != null ? detail.getSelectionCriteria() : "")
                .precautions(detail != null ? detail.getPrecautions() : "")
                .createdBy("")
                .createdAt(event.getCreatedBy() != null ? event.getCreatedBy().toString() : null)
                .purchasePeriod(purchasePeriod)
                .votePeriod(votePeriod)
                .announcementDate(announcementDate)
                .build();
    }
} 