package com.cMall.feedShop.event.application.service;

import com.cMall.feedShop.event.application.dto.request.EventCreateRequestDto;
import com.cMall.feedShop.event.application.exception.EventException;
import org.springframework.stereotype.Component;

@Component
public class EventValidator {

    /**
     * 이벤트 생성 요청 검증
     */
    public void validateEventCreateRequest(EventCreateRequestDto requestDto) {
        if (requestDto.getType() == null) {
            throw new EventException.InvalidEventTypeException();
        }
        
        if (requestDto.getStatus() == null) {
            throw new EventException.InvalidEventStatusException();
        }
        
        if (requestDto.getTitle() == null || requestDto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("이벤트 제목은 필수입니다.");
        }
        
        if (requestDto.getDescription() == null || requestDto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("이벤트 설명은 필수입니다.");
        }
        
        // 날짜 검증
        if (requestDto.getEventStartDate() != null && requestDto.getEventEndDate() != null) {
            if (requestDto.getEventStartDate().isAfter(requestDto.getEventEndDate())) {
                throw new IllegalArgumentException("이벤트 시작일은 종료일보다 이전이어야 합니다.");
            }
        }
        
        if (requestDto.getPurchaseStartDate() != null && requestDto.getPurchaseEndDate() != null) {
            if (requestDto.getPurchaseStartDate().isAfter(requestDto.getPurchaseEndDate())) {
                throw new IllegalArgumentException("구매 시작일은 종료일보다 이전이어야 합니다.");
            }
        }
    }
} 