package com.example.notification.service.sender;

import com.example.notification.common.enums.ChannelType;
import com.example.notification.common.enums.NotificationStatus;
import com.example.notification.entity.Notification;
import com.example.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsSender implements NotificationSender {

    private final NotificationRepository notificationRepository;

    @Override
    public ChannelType getSupportedChannel() {
        return ChannelType.SMS;
    }

    @Override
    public void send(Notification notification) {
        try {

            log.info("Sending SMS to {}: {}", notification.getRecipientAddress(), notification.getContent());

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setRetryCount(notification.getRetryCount() + 1);

            String error = e.getMessage() != null ? e.getMessage() : "Unknown SMS Error";
            notification.setErrorMessage(error.substring(0, Math.min(error.length(), 255)));
        }
        notificationRepository.save(notification);
    }
}