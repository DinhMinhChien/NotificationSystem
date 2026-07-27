package com.example.notification.service.retry;

import com.example.notification.common.enums.NotificationStatus;
import com.example.notification.entity.Notification;
import com.example.notification.repository.NotificationRepository;
import com.example.notification.service.sender.NotificationSenderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRetryScheduler {

    private final NotificationRepository notificationRepository;
    private final NotificationSenderFactory notificationSenderFactory;

    @Scheduled(fixedDelay = 30000)
    public void retryFailedNotifications() {

        int maxRetry = 3;

        List<Notification> notifications =
                notificationRepository.findAllByStatusAndRetryCountLessThan(
                        NotificationStatus.FAILED,
                        maxRetry
                );

        for (Notification notification : notifications) {

            log.info(
                    "Retry notification {}, attempt {}",
                    notification.getId(),
                    notification.getRetryCount() + 1
            );

            notificationSenderFactory
                    .getSender(notification.getChannel())
                    .send(notification);
        }
    }
}