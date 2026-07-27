package com.example.notification.dto.response;

import com.example.notification.common.enums.CampaignStatus;
import com.example.notification.common.enums.ScheduleType;
import com.example.notification.common.enums.TargetType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CampaignListResponse {
    private String id;
    private String name;
    private String templateCode;
    private TargetType targetType;
    private ScheduleType scheduleType;
    private CampaignStatus status;
    private LocalDateTime scheduledAt;
}
