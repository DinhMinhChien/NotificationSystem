package com.example.notification.dto.request;

import com.example.notification.common.enums.ScheduleType;
import com.example.notification.common.enums.TargetType;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Campaign's name not null or empty")
    private String name ;

    @NotBlank(message = "Template id not null or empty")
    private String templateId ;

    @NotBlank(message = "Target type not null or empty")
    private TargetType targetType ;
    private String targetUserId;
    private String targetGroupId;
    private String conditionExpression;

    @NotBlank(message = "Schedule type not null")
    private ScheduleType scheduleType;

    private LocalDateTime scheduledAt;
    private String cronExpression;
}
