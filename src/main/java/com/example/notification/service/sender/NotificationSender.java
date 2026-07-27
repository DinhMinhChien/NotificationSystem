package com.example.notification.service.sender;

import com.example.notification.common.enums.ChannelType;
import com.example.notification.entity.Notification;

public interface NotificationSender {
    ChannelType getSupportedChannel();
    void send(Notification notification) ;
}
