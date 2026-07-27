package com.example.notification.service;

import com.example.notification.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void processCampaign(String campaignId) ;
    List<NotificationResponse> getByUser(String userId) ;
}
