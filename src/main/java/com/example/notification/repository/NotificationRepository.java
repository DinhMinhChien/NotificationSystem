package com.example.notification.repository;

import com.example.notification.common.enums.NotificationStatus;
import com.example.notification.entity.Notification;
import com.example.notification.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    boolean existsByIdempotencyKey(String idempotencyKey);
    List<Notification> findAllByStatusAndRetryCountLessThan(
            NotificationStatus status,
            int retryCount
    );

    List<Notification> findAllByUser(User user);
}
