package com.example.notification.dto.request;

import com.example.notification.common.enums.ScheduleType;
import com.example.notification.common.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignCreateRequest {
    private String name ;
    private String templateId ;

    private TargetType targetType ;
    private String targetUserId;
    private String targetGroupId;
    private String conditionExpression;

    private ScheduleType scheduleType;
    private LocalDateTime scheduledAt;
    private String cronExpression;
}
