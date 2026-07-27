package com.example.notification.kafka.consumer;

import com.example.notification.kafka.dto.NotificationEvent;
import com.example.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationService notificationService ;

    @KafkaListener(topics = "notification-topic")
    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 2000,multiplier = 2.0 ),
            exclude = {NullPointerException.class, RuntimeException.class}
    )
    public void handleNotification(NotificationEvent event) {
        notificationService.processCampaign(event.getCampaignId()) ;
    }
}
