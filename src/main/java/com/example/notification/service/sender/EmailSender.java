package com.example.notification.service.sender;

import com.example.notification.common.enums.ChannelType;
import com.example.notification.common.enums.NotificationStatus;
import com.example.notification.entity.Notification;
import com.example.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailSender implements NotificationSender {
    private final JavaMailSender javaMailSender ;
    private final NotificationRepository notificationRepository ;

    @Override
    public ChannelType getSupportedChannel() {
        return ChannelType.EMAIL ;
    }

    @Override
    public void send(Notification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage() ;
            message.setTo(notification.getRecipientAddress());
            message.setSubject(notification.getTitle());
            message.setText(notification.getContent());

            javaMailSender.send(message);

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setRetryCount(notification.getRetryCount() + 1);
            notification.setErrorMessage(e.getMessage());
        }
        notificationRepository.save(notification);
    }
}
