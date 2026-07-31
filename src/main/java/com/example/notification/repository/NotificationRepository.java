package com.example.notification.repository;

import com.example.notification.common.enums.NotificationStatus;
import com.example.notification.entity.Notification;
import com.example.notification.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface NotificationRepository extends JpaRepository<Notification, String>, JpaSpecificationExecutor<Notification> {
    boolean existsByIdempotencyKey(String idempotencyKey);
    List<Notification> findAllByStatusAndRetryCountLessThan(
            NotificationStatus status,
            int retryCount
    );

    Page<Notification> findAllByUser(User user, Pageable pageable);

    int countAllByStatus(NotificationStatus status);

    int countAllByDeleted(Boolean deleted);

    int countAllByIsRead(Boolean isRead);

    @Query("SELECT n.idempotencyKey FROM Notification n WHERE n.idempotencyKey IN :keys")
    Set<String> findAllIdempotencyKeyIn(Collection<String> keys);
}
