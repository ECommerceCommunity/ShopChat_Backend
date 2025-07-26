package com.cMall.feedShop.event.application.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FeedEventDto {
    private Long eventId;
    private String title;
    private String eventStartDate;
    private String eventEndDate;
    private String type;
} 