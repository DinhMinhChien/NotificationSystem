package com.example.notification.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String campaignId ;

    private Map<String, String> payload;

    public NotificationEvent(String campaignId) {
        this.campaignId = campaignId;
    }
}
