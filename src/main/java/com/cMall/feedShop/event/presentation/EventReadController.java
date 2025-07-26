package com.cMall.feedShop.event.presentation;

import com.cMall.feedShop.event.application.dto.request.EventListRequestDto;
import com.cMall.feedShop.event.application.dto.response.EventListResponseDto;
import com.cMall.feedShop.event.application.service.EventReadService;
import com.cMall.feedShop.common.dto.ApiResponse;
import com.cMall.feedShop.event.application.dto.response.FeedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventReadController {
    private final EventReadService eventReadService;

    /**
     * 전체 이벤트 목록 조회 (페이징)
     */
    @GetMapping("/all")
    public EventListResponseDto getAllEvents(@RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer size) {
        return eventReadService.getAllEvents(page, size);
    }

    /**
     * 이벤트 검색/필터/정렬 (QueryDSL 기반)
     */
    @GetMapping("/search")
    public EventListResponseDto searchEvents(@ModelAttribute EventListRequestDto requestDto) {
        return eventReadService.searchEvents(requestDto);
    }

    /**
     * 피드 생성용 이벤트 목록 조회 (진행중인 이벤트만)
     */
    @GetMapping("/feed-available")
    public ApiResponse<List<FeedEventDto>> getFeedAvailableEvents() {
        List<FeedEventDto> events = eventReadService.getFeedAvailableEvents();
        return ApiResponse.<List<FeedEventDto>>builder()
                .success(true)
                .message("피드 생성 가능한 이벤트 목록을 성공적으로 조회했습니다.")
                .data(events)
                .build();
    }
} 