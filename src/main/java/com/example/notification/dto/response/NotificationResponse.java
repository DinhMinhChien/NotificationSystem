package com.example.notification.dto.response;

import com.example.notification.common.enums.ChannelType;
import com.example.notification.common.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private String fullName ;

    private NotificationType notificationType ;

    private ChannelType channel ;

    private String recipientAddress ;

    private String title ;

    private String content ;

    private LocalDateTime sentAt ;
}
