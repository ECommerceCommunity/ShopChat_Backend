package com.cMall.feedShop.event.presentation;

import com.cMall.feedShop.common.dto.ApiResponse;
import com.cMall.feedShop.event.application.dto.request.EventUpdateRequestDto;
import com.cMall.feedShop.event.application.service.EventUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventUpdateController {
    private final EventUpdateService eventUpdateService;
    private final EventDeleteService eventDeleteService;

    /**
     * 이벤트 수정
     */
    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventUpdateRequestDto requestDto
    ) {
        requestDto.setEventId(eventId);
        eventUpdateService.updateEvent(requestDto);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("이벤트가 성공적으로 수정되었습니다.")
                .build();
        return ResponseEntity.ok(response);
    }

} 