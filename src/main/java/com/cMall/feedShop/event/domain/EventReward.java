package com.cMall.feedShop.event.domain;

import com.cMall.feedShop.event.domain.enums.ConditionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_rewards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_reward_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private RewardType rewardType;

    @Column(name = "feed_id")
    private Long feedId; // FK 연결 가능

    @Column(name = "userprofile_id")
    private Long userProfileId; // FK 연결 가능

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    private ConditionType conditionType;

    @Column(name = "condition_value")
    private Integer conditionValue;

    @Column(name = "reward_value")
    private String rewardValue;

    @Column(name = "max_recipients")
    private Integer maxRecipients;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
