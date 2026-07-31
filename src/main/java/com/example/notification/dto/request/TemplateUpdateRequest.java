package com.example.notification.dto.request;

import com.example.notification.common.enums.ChannelType;
import com.example.notification.common.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateUpdateRequest {

    @NotBlank(message = "Template code not null or empty")
    private String code ;

    @NotBlank(message = "Channel not null or empty")
    private ChannelType channel ;

    @NotBlank(message = "Notification type not null or empty")
    private NotificationType notificationType;

    @NotBlank(message = "Require language")
    private String language ;

    @NotBlank(message = "Subject template not null or empty")
    private String subject ;

    @NotBlank(message = "Content message not null or empty")
    private String content ;

    @NotBlank(message = "Require field isActive not null or empty")
    private Boolean isActive ;
}
