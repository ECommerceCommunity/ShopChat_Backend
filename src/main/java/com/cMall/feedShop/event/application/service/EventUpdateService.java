package com.cMall.feedShop.event.application.service;

import com.cMall.feedShop.event.application.dto.request.EventUpdateRequestDto;
import com.cMall.feedShop.event.domain.Event;
import com.cMall.feedShop.event.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventUpdateService {
    private final EventRepository eventRepository;

    /**
     * 이벤트 수정 비즈니스 로직
     */
    @Transactional
    public void updateEvent(EventUpdateRequestDto dto) {
        Event event = eventRepository.findDetailById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다.")); // 예외는 다음 단계에서 커스텀 처리 예정
        event.updateFromDto(dto);
        eventRepository.save(event);
    }
} 