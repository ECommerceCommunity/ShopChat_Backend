package com.cMall.feedShop.event.application.dto.request;

import lombok.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EventUpdateRequestDto {
    private Long eventId;
    private String title;
    private String description;
    private String type;
    private String status;
    private Integer maxParticipants;
    private LocalDate purchaseStartDate;
    private LocalDate purchaseEndDate;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private LocalDate announcementDate;
    private String participationMethod;
    private String selectionCriteria;
    private String imageUrl;
    private String precautions;
    private String rewards;
} 