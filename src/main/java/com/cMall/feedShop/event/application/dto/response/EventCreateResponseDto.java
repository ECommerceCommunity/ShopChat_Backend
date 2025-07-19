// 이벤트 생성 응답 DTO
package com.cMall.feedShop.event.application.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EventCreateResponseDto {
    
    private Long eventId;
    private String title;
    private String type;
    private String status;
    private Integer maxParticipants;
    private LocalDateTime createdAt;
    private String message;
    
    public static EventCreateResponseDto of(Long eventId, String title, String type, String status, 
                                         Integer maxParticipants, LocalDateTime createdAt) {
        return EventCreateResponseDto.builder()
                .eventId(eventId)
                .title(title)
                .type(type)
                .status(status)
                .maxParticipants(maxParticipants)
                .createdAt(createdAt)
                .message("이벤트가 성공적으로 생성되었습니다.")
                .build();
    }
} 