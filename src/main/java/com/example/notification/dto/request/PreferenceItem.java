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
public class PreferenceItem {

    @NotBlank(message = "Notification type not null or empty")
    private NotificationType notiType;

    @NotBlank(message = "Channel not null or empty")
    private ChannelType channel;

    @NotBlank(message = "Require field enable")
    private Boolean enabled;
}
