package com.example.notification.service;

import com.example.notification.dto.response.NotificationDashboard;
import com.example.notification.dto.response.NotificationResponse;
import com.example.notification.kafka.dto.NotificationEvent;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NotificationService {
    void processCampaign(NotificationEvent event) ;
    Page<NotificationResponse> getByUser(String userId, int pageNumber, int pageSize) ;
    NotificationDashboard summary() ;
}
