package com.example.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDashboard {
    private long totalNotifications;
    private long totalSent;
    private long totalFailed;
    private long totalRead;
    private double successRate; // Phần trăm (%)
    private double errorRate;   // Phần trăm (%)
    private double readRate;
}
