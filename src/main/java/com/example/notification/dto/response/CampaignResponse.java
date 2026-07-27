package com.example.notification.dto.response;

import com.example.notification.common.enums.CampaignStatus;
import com.example.notification.common.enums.ScheduleType;
import com.example.notification.common.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignResponse {
    private String id;
    private String name;
    private String templateId;
    private String templateCode;
    private TargetType targetType;
    private String targetUserId;
    private String targetGroupId;
    private String targetGroupName;
    private String conditionExpression;
    private ScheduleType scheduleType;
    private LocalDateTime scheduledAt;
    private String cronExpression;
    private CampaignStatus status;
    private LocalDateTime createdAt;


}
