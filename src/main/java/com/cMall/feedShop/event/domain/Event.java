package com.cMall.feedShop.event.domain;

import com.cMall.feedShop.event.domain.enums.EventStatus;
import com.cMall.feedShop.event.domain.enums.EventType;
import com.cMall.feedShop.user.domain.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.cMall.feedShop.common.BaseTimeEntity;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Event extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "created_by", updatable = false)
    private LocalDateTime createdBy;

    @Column(name = "updated_by")
    private LocalDateTime updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User createdUser;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private EventDetail eventDetail;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventReward> rewards;

    // 연관관계 설정 메서드
    public void setEventDetail(EventDetail eventDetail) {
        this.eventDetail = eventDetail;
        if (eventDetail != null) {
            eventDetail.setEvent(this);
        }
    }

    public void setRewards(List<EventReward> rewards) {
        this.rewards = rewards;
        if (rewards != null) {
            rewards.forEach(reward -> reward.setEvent(this));
        }
    }

    /**
     * 이벤트 상태를 자동으로 계산하여 업데이트
     */
    public void updateStatusAutomatically() {
        if (eventDetail == null) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate eventStartDate = eventDetail.getEventStartDate();
        LocalDate eventEndDate = eventDetail.getEventEndDate();

        if (eventStartDate == null || eventEndDate == null) {
            return;
        }

        if (today.isBefore(eventStartDate)) {
            this.status = EventStatus.UPCOMING;
        } else if (today.isAfter(eventEndDate)) {
            this.status = EventStatus.ENDED;
        } else {
            this.status = EventStatus.ONGOING;
        }
    }

    /**
     * 현재 상태가 자동 계산된 상태와 일치하는지 확인
     */
    public boolean isStatusUpToDate() {
        EventStatus calculatedStatus = calculateStatus();
        return this.status == calculatedStatus;
    }

    /**
     * 현재 날짜 기준으로 이벤트 상태 계산
     */
    public EventStatus calculateStatus() {
        if (eventDetail == null) {
            return this.status; // 기존 상태 유지
        }

        LocalDate today = LocalDate.now();
        LocalDate eventStartDate = eventDetail.getEventStartDate();
        LocalDate eventEndDate = eventDetail.getEventEndDate();

        if (eventStartDate == null || eventEndDate == null) {
            return this.status; // 기존 상태 유지
        }

        if (today.isBefore(eventStartDate)) {
            return EventStatus.UPCOMING;
        } else if (today.isAfter(eventEndDate)) {
            return EventStatus.ENDED;
        } else {
            return EventStatus.ONGOING;
        }
    }

    // 기타 연관관계 필요시 여기에 추가
}
