package com.example.notification.dto.request;

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
public class PreferenceItem {
    private NotificationType notiType;

    private ChannelType channel;

    private Boolean enabled;
}
