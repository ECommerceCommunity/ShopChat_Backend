package com.cMall.feedShop.event.application.service;

import com.cMall.feedShop.event.application.dto.response.EventSummaryDto;
import com.cMall.feedShop.event.domain.Event;
import com.cMall.feedShop.event.domain.EventDetail;
import com.cMall.feedShop.event.domain.EventReward;
import com.cMall.feedShop.event.domain.enums.EventStatus;
import com.cMall.feedShop.event.domain.enums.EventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class EventMapperTest {
    private final EventMapper mapper = new EventMapper();

    @Test
    void toSummaryDto_모든필드_정상매핑() {
        Event event = Event.builder()
                .id(1L)
                .type(EventType.BATTLE)
                .status(EventStatus.UPCOMING)
                .maxParticipants(100)
                .createdBy(LocalDateTime.now())
                .build();
        EventDetail detail = EventDetail.builder()
                .title("이벤트 제목")
                .description("이벤트 설명")
                .eventStartDate(LocalDate.now())
                .eventEndDate(LocalDate.now().plusDays(7))
                .imageUrl("/images/event.jpg")
                .participationMethod("참여 방법")
                .selectionCriteria("선정 기준")
                .precautions("주의사항")
                .purchaseStartDate(LocalDate.now())
                .purchaseEndDate(LocalDate.now().plusDays(2))
                .announcement(LocalDate.now().plusDays(8))
                .rewards("1등: 상품")
                .build();
        event.setEventDetail(detail);
        EventReward reward = EventReward.builder()
                .conditionValue(1)
                .rewardValue("상품권")
                .build();
        event.setRewards(List.of(reward));
        EventSummaryDto dto = mapper.toSummaryDto(event);
        assertThat(dto.getEventId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("이벤트 제목");
        assertThat(dto.getType()).isEqualTo("battle");
        assertThat(dto.getStatus()).isEqualTo("ongoing");
        assertThat(dto.getEventStartDate()).isEqualTo(LocalDate.now().toString());
        assertThat(dto.getEventEndDate()).isEqualTo(LocalDate.now().plusDays(7).toString());
        assertThat(dto.getImageUrl()).isEqualTo("/images/event.jpg");
        assertThat(dto.getMaxParticipants()).isEqualTo(100);
        assertThat(dto.getDescription()).isEqualTo("이벤트 설명");
        assertThat(dto.getRewards()).hasSize(1);
        assertThat(dto.getRewards().get(0).getRank()).isEqualTo(1);
        assertThat(dto.getRewards().get(0).getReward()).isEqualTo("상품권");
        assertThat(dto.getParticipationMethod()).isEqualTo("참여 방법");
        assertThat(dto.getSelectionCriteria()).isEqualTo("선정 기준");
        assertThat(dto.getPrecautions()).isEqualTo("주의사항");
        assertThat(dto.getCreatedBy()).isEqualTo("");
        assertThat(dto.getCreatedAt()).isNotNull(); // createdBy(LocalDateTime.now())
        assertThat(dto.getPurchasePeriod()).isEqualTo(LocalDate.now() + " ~ " + LocalDate.now().plusDays(2));
        assertThat(dto.getVotePeriod()).isEqualTo(LocalDate.now() + " ~ " + LocalDate.now().plusDays(7));
        assertThat(dto.getAnnouncementDate()).isEqualTo(LocalDate.now().plusDays(8).toString());
    }

    @Test
    void toSummaryDto_nullSafe_테스트() {
        Event event = Event.builder()
                .id(2L)
                .type(null)
                .status(null)
                .maxParticipants(null)
                .createdBy(null)
                .eventDetail(null)
                .rewards(null)
                .build();
        EventSummaryDto dto = mapper.toSummaryDto(event);
        assertThat(dto.getType()).isNull();
        assertThat(dto.getStatus()).isNull();
        assertThat(dto.getTitle()).isEqualTo("");
        assertThat(dto.getDescription()).isEqualTo("");
        assertThat(dto.getRewards()).isEmpty();
        assertThat(dto.getPurchasePeriod()).isEqualTo("");
        assertThat(dto.getVotePeriod()).isEqualTo("");
        assertThat(dto.getAnnouncementDate()).isEqualTo("");
    }
} 