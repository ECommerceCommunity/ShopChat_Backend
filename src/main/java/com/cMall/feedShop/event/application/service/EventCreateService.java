package com.cMall.feedShop.event.application.service;

import com.cMall.feedShop.event.application.dto.request.EventCreateRequestDto;
import com.cMall.feedShop.event.application.dto.response.EventCreateResponseDto;
import com.cMall.feedShop.event.application.exception.EventException;
import com.cMall.feedShop.event.domain.Event;
import com.cMall.feedShop.event.domain.EventDetail;
import com.cMall.feedShop.event.domain.EventReward;
import com.cMall.feedShop.event.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCreateService {
    private final EventRepository eventRepository;
    private final EventValidator eventValidator;

    /**
     * 이벤트 생성
     */
    @Transactional
    public EventCreateResponseDto createEvent(EventCreateRequestDto requestDto) {
        // 입력값 검증
        eventValidator.validateEventCreateRequest(requestDto);
        
        // Event 엔티티 생성
        Event event = Event.builder()
                .type(requestDto.getType())
                .status(requestDto.getStatus())
                .maxParticipants(requestDto.getMaxParticipants())
                .createdBy(LocalDateTime.now())
                .build();
        
        // EventDetail 엔티티 생성
        EventDetail eventDetail = EventDetail.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .imageUrl(requestDto.getImageUrl())
                .participationMethod(requestDto.getParticipationMethod())
                .selectionCriteria(requestDto.getSelectionCriteria())
                .precautions(requestDto.getPrecautions())
                .purchaseStartDate(requestDto.getPurchaseStartDate())
                .purchaseEndDate(requestDto.getPurchaseEndDate())
                .eventStartDate(requestDto.getEventStartDate())
                .eventEndDate(requestDto.getEventEndDate())
                .announcement(requestDto.getAnnouncement())
                .build();
        
        // EventReward 엔티티들 생성
        List<EventReward> rewards = requestDto.getRewards() != null ? 
                requestDto.getRewards().stream()
                        .map(rewardDto -> EventReward.builder()
                                .conditionValue(rewardDto.getConditionValue())
                                .rewardValue(rewardDto.getRewardValue())
                                .build())
                        .toList() : Collections.emptyList();
        
        // 연관관계 설정
        event.setEventDetail(eventDetail);
        event.setRewards(rewards);
        
        // 저장
        Event savedEvent = eventRepository.save(event);
        
        return EventCreateResponseDto.of(
                savedEvent.getId(),
                savedEvent.getEventDetail().getTitle(),
                savedEvent.getType().name().toLowerCase(),
                savedEvent.getStatus().name().toLowerCase(),
                savedEvent.getMaxParticipants(),
                savedEvent.getCreatedBy()
        );
    }
} 