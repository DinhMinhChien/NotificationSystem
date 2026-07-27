package com.example.notification.entity;

import com.example.notification.common.enums.CampaignStatus;
import com.example.notification.common.enums.ScheduleType;
import com.example.notification.common.enums.TargetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "campaigns")
public class Campaign extends BaseEntity{
    @Id
    @UuidGenerator
    @Column(name = "id")
    private String id ;

    @Column(name = "name")
    private String name ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private Template template ;

    @Column(name = "target_type")
    @Enumerated(EnumType.STRING)
    private TargetType targetType ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_group_id")
    private Group targetGroup;

    private String conditionExpression;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    private LocalDateTime scheduledAt;

    private String cronExpression;

    @Enumerated(EnumType.STRING)
    private CampaignStatus status;


}
