package com.cMall.feedShop.event.domain;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import com.cMall.feedShop.common.BaseTimeEntity;

@Entity
@Table(name = "event_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EventDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_details_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "purchase_start_date")
    private LocalDate purchaseStartDate;

    @Column(name = "purchase_end_date")
    private LocalDate purchaseEndDate;

    @Column(name = "event_start_date")
    private LocalDate eventStartDate;

    @Column(name = "event_end_date")
    private LocalDate eventEndDate;

    @Column(name = "announcement")
    private LocalDate announcement;

    @Column(name = "participation_method", columnDefinition = "TEXT")
    private String participationMethod;

    @Column(name = "selection_criteria", columnDefinition = "TEXT")
    private String selectionCriteria;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "precautions", columnDefinition = "TEXT")
    private String precautions;

    @Column(name = "rewards", columnDefinition = "TEXT")
    private String rewards;

    // 연관관계 설정 메서드
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * 이벤트 상세 정보 수정 (빌더 패턴 활용)
     */
    public void updateFromDto(com.cMall.feedShop.event.application.dto.request.EventUpdateRequestDto dto) {
        this.title = dto.getTitle() != null ? dto.getTitle() : this.title;
        this.description = dto.getDescription() != null ? dto.getDescription() : this.description;
        this.purchaseStartDate = dto.getPurchaseStartDate() != null ? dto.getPurchaseStartDate() : this.purchaseStartDate;
        this.purchaseEndDate = dto.getPurchaseEndDate() != null ? dto.getPurchaseEndDate() : this.purchaseEndDate;
        this.eventStartDate = dto.getEventStartDate() != null ? dto.getEventStartDate() : this.eventStartDate;
        this.eventEndDate = dto.getEventEndDate() != null ? dto.getEventEndDate() : this.eventEndDate;
        this.announcement = dto.getAnnouncementDate() != null ? dto.getAnnouncementDate() : this.announcement;
        this.participationMethod = dto.getParticipationMethod() != null ? dto.getParticipationMethod() : this.participationMethod;
        this.selectionCriteria = dto.getSelectionCriteria() != null ? dto.getSelectionCriteria() : this.selectionCriteria;
        this.imageUrl = dto.getImageUrl() != null ? dto.getImageUrl() : this.imageUrl;
        this.precautions = dto.getPrecautions() != null ? dto.getPrecautions() : this.precautions;
        this.rewards = dto.getRewards() != null ? dto.getRewards() : this.rewards;
    }
}
