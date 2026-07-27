package com.example.notification.service.sender;

import com.example.notification.common.enums.ChannelType;
import com.example.notification.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NotificationSenderFactory {
    private final Map<ChannelType, NotificationSender> senderMap;
    public NotificationSenderFactory(List<NotificationSender> senders) {
        senderMap = senders.stream()
                .collect(Collectors.toMap( NotificationSender::getSupportedChannel, Function.identity() ));
    }
    public NotificationSender getSender(ChannelType channel) {
        NotificationSender sender = senderMap.get(channel);
        if (sender == null) {
            throw new BusinessException("Notification sender is not supported for channel: " + channel);
        }
        return sender;
    }
}
