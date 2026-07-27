package com.example.notification.repository;

import com.example.notification.entity.User;
import com.example.notification.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference,String> {
    List<UserPreference> findAllByUser(User user);
    Optional<UserPreference> findByUserAndNotificationTypeAndChannel(
            User user,
            com.example.notification.common.enums.NotificationType notificationType,
            com.example.notification.common.enums.ChannelType channel
    );

}
