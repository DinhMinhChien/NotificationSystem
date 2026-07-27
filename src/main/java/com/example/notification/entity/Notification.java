package com.example.notification.entity;

import com.example.notification.common.enums.ChannelType;
import com.example.notification.common.enums.NotificationStatus;
import com.example.notification.common.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity{
    @Id
    @UuidGenerator
    @Column(name = "id")
    private String id ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign ;

    @Column(name = "notification_type")
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType ;

    @Column(name = "channel")
    @Enumerated(EnumType.STRING)
    private ChannelType channel ;

    @Column(name = "idempotency_key")
    private String idempotencyKey ;

    @Column(name = "recipient_address")
    private String recipientAddress ;

    @Column(name = "title")
    private String title ;

    @Column(name = "content")
    private String content ;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NotificationStatus status ;

    @Column(name = "retry_count")
    private int retryCount ;

    @Column(name = "error_message")
    private String errorMessage ;

    @Column(name = "is_read")
    private Boolean isRead ;

    @Column(name = "read_at")
    private LocalDateTime readAt ;

    @Column(name = "sent_at")
    private LocalDateTime sentAt ;

}
