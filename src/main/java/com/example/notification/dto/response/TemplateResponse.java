package com.example.notification.dto.response;

import com.example.notification.common.enums.ChannelType;
import com.example.notification.common.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateResponse {
    private String id ;
    private String code ;
    private ChannelType channel ;
    private NotificationType notificationType;
    private String language ;
    private String subject ;
    private String content ;
    private Boolean isActive ;
}
