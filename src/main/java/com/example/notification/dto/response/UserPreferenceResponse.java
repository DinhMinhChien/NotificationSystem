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
public class UserPreferenceResponse {
    private NotificationType notificationType;
    private ChannelType channel;
    private Boolean isEnabled;
}
