// 이벤트 관련 API 요청을 처리하는 컨트롤러 클래스
package com.cMall.feedShop.event.presentation;

import com.cMall.feedShop.event.application.dto.request.EventListRequestDto;
import com.cMall.feedShop.event.application.dto.response.EventListResponseDto;
import com.cMall.feedShop.event.application.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    /**
     * 전체 이벤트 목록 조회 (페이징)
     */
    @GetMapping("/all")
    public EventListResponseDto getAllEvents(@RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer size) {
        return eventService.getAllEvents(page, size);
    }

    /**
     * 이벤트 검색/필터/정렬 (QueryDSL 기반)
     */
    @GetMapping("/search")
    public EventListResponseDto searchEvents(@ModelAttribute EventListRequestDto requestDto) {
        return eventService.searchEvents(requestDto);
    }
} 