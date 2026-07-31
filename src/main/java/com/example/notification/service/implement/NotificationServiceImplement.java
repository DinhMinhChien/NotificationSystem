package com.example.notification.service.implement;

import com.example.notification.common.enums.CampaignStatus;
import com.example.notification.common.enums.ChannelType;
import com.example.notification.common.enums.NotificationStatus;
import com.example.notification.common.enums.NotificationType;
import com.example.notification.common.exception.BusinessException;
import com.example.notification.dto.response.NotificationDashboard;
import com.example.notification.dto.response.NotificationResponse;
import com.example.notification.entity.*;
import com.example.notification.kafka.dto.NotificationEvent;
import com.example.notification.mapper.NotificationMapper;
import com.example.notification.repository.*;
import com.example.notification.service.NotificationService;
import com.example.notification.service.sender.NotificationSenderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImplement implements NotificationService {

    private final CampaignRepository campaignRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final NotificationSenderFactory notificationSenderFactory;
    private final NotificationMapper notificationMapper;
    private final TransactionTemplate transactionTemplate;

    private static final Pattern TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_.-]+)\\s*}}");

    @Override
    public void processCampaign(NotificationEvent event) {
        String campaignId = event.getCampaignId();
        Map<String, String> payload = event.getPayload();

        // 1. Khởi tạo & Lưu các bản ghi Notification vào DB (Chạy trong Transaction ngắn)
        List<Notification> notificationsToSend = transactionTemplate.execute(status -> {
            Campaign campaign = campaignRepository.findById(campaignId)
                    .orElseThrow(() -> new BusinessException("Campaign not found"));

            Template template = campaign.getTemplate();
            if (template == null || Boolean.FALSE.equals(template.getIsActive())) {
                throw new BusinessException("Campaign template is not active");
            }

            List<User> recipients = switch (campaign.getTargetType()) {
                case USER -> List.of(campaign.getTargetUser());
                case GROUP -> groupMemberRepository.findAllByGroup(campaign.getTargetGroup())
                        .stream().map(GroupMember::getUser).toList();
                case ALL -> userRepository.findAll();
                case CONDITION -> throw new UnsupportedOperationException("Condition target type is not implemented yet");
            };

            NotificationType notificationType = template.getNotificationType();
            ChannelType channel = template.getChannel();

            // Lọc danh sách User hợp lệ
            List<User> validUsers = recipients.stream()
                    .filter(Objects::nonNull)
                    .filter(user -> !Boolean.FALSE.equals(user.getIsActive())) // lọc user khong còn hoạt động
                    .filter(user -> hasRecipientAddress(user, channel)) // lọc user không có địa chỉ gửi (email,sdt ,...)
                    .filter(user -> isPreferenceEnabled(user, notificationType, channel)) // lọc user không bật cấu hình nhận thông bao
                    .toList();

            // Build danh sách Notification tạm thời
            List<Notification> candidateNotifications = validUsers.stream()
                    .map(user -> buildNotification(campaign, template, user, notificationType, channel, payload))
                    .toList();

            if (candidateNotifications.isEmpty()) {
                updateCampaignStatus(campaign);
                return List.of();
            }

            //  Lấy tất cả idempotencyKey đã tồn tại
            Set<String> candidateKeys = candidateNotifications.stream()
                    .map(Notification::getIdempotencyKey)
                    .collect(Collectors.toSet());

            Set<String> existingKeys = notificationRepository.findAllIdempotencyKeyIn(candidateKeys);

            // Filter bỏ các bản ghi đã tồn tại
            List<Notification> finalNotifications = candidateNotifications.stream()
                    .filter(n -> !existingKeys.contains(n.getIdempotencyKey()))
                    .toList();

            notificationRepository.saveAll(finalNotifications);
            updateCampaignStatus(campaign);

            return finalNotifications;
        });

        if (notificationsToSend == null || notificationsToSend.isEmpty()) {
            return;
        }

        // 2. Gửi Email/SMS NGOÀI Transaction (Tránh treo DB Connection Pool)
        for (Notification notification : notificationsToSend) {
            try {
                notificationSenderFactory
                        .getSender(notification.getChannel())
                        .send(notification);
            } catch (Exception e) {
                log.error("Failed to send notification ID {}: {}", notification.getId(), e.getMessage());
            }
        }
    }

    private void updateCampaignStatus(Campaign campaign) {
        switch (campaign.getScheduleType()) {
            case IMMEDIATE, ONCE -> campaign.setStatus(CampaignStatus.COMPLETED);
            case RECURRING -> {
                CronExpression cron = CronExpression.parse(campaign.getCronExpression());
                campaign.setScheduledAt(cron.next(campaign.getScheduledAt()));
                campaign.setStatus(CampaignStatus.SCHEDULED);
            }
        }
        campaignRepository.save(campaign);
    }

    private boolean isPreferenceEnabled(User user, NotificationType notificationType, ChannelType channel) {
        return userPreferenceRepository.findByUserAndNotificationTypeAndChannel(user, notificationType, channel)
                .map(preference -> !Boolean.FALSE.equals(preference.getIsEnabled()))
                .orElse(true);
    }

    private Notification buildNotification(
            Campaign campaign,
            Template template,
            User user,
            NotificationType notificationType,
            ChannelType channel,
            Map<String, String> payload
    ) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setCampaign(campaign);
        notification.setNotificationType(notificationType);
        notification.setChannel(channel);
        notification.setIdempotencyKey(buildIdempotencyKey(campaign, user, channel));
        notification.setRecipientAddress(resolveRecipientAddress(user, channel));

        Map<String, String> variables = buildVariables(campaign, template, user, payload);
        notification.setTitle(render(template.getSubject(), variables));
        notification.setContent(render(template.getContent(), variables));
        notification.setStatus(NotificationStatus.QUEUED);
        notification.setRetryCount(0);
        notification.setIsRead(false);
        return notification;
    }

    private String buildIdempotencyKey(Campaign campaign, User user, ChannelType channel) {
        LocalDateTime scheduledAt = campaign.getScheduledAt();
        String runKey = scheduledAt == null ? "immediate" : scheduledAt.toString();
        return campaign.getId() + ":" + user.getId() + ":" + channel + ":" + runKey;
    }

    private String resolveRecipientAddress(User user, ChannelType channel) {
        return switch (channel) {
            case EMAIL -> user.getEmail();
            case SMS -> user.getPhone();
            case PUSH -> user.getDeviceToken();
        };
    }

    private boolean hasRecipientAddress(User user, ChannelType channel) {
        String recipientAddress = resolveRecipientAddress(user, channel);
        return recipientAddress != null && !recipientAddress.isBlank();
    }

    private Map<String, String> buildVariables(Campaign campaign, Template template, User user, Map<String, String> payload) {
        Map<String, String> variables = new HashMap<>();
        variables.put("user_id", nullToEmpty(user.getId()));
        variables.put("user_name", nullToEmpty(user.getFullName()));
        variables.put("email", nullToEmpty(user.getEmail()));
        variables.put("phone", nullToEmpty(user.getPhone()));
        variables.put("campaign_id", nullToEmpty(campaign.getId()));
        variables.put("campaign_name", nullToEmpty(campaign.getName()));
        variables.put("template_code", nullToEmpty(template.getCode()));

        // Nạp thêm tất cả dữ liệu động từ payload (Order, Payment...)
        if (payload != null && !payload.isEmpty()) {
            variables.putAll(payload);
        }

        return variables;
    }

    private String render(String text, Map<String, String> variables) {
        if (text == null || text.isBlank()) {
            return text;
        }

        Matcher matcher = TEMPLATE_VARIABLE_PATTERN.matcher(text);
        StringBuilder rendered = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            matcher.appendReplacement(rendered, Matcher.quoteReplacement(variables.getOrDefault(key, "")));
        }
        matcher.appendTail(rendered);
        return rendered.toString();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    @Override
    public Page<NotificationResponse> getByUser(String userId, int pageNumber, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Not found user"));

        return notificationRepository.findAllByUser(user, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    public NotificationDashboard summary() {
        int totalNotifications = notificationRepository.countAllByDeleted(false);
        if (totalNotifications == 0) {
            return new NotificationDashboard();
        }

        int totalSent = notificationRepository.countAllByStatus(NotificationStatus.SENT);
        int totalFailed = notificationRepository.countAllByStatus(NotificationStatus.FAILED);
        int totalRead = notificationRepository.countAllByIsRead(true);

        double successRate = (double) totalSent / totalNotifications;
        double errorRate = (double) totalFailed / totalNotifications;
        double readRate = (double) totalRead / totalNotifications;

        NotificationDashboard notificationDashboard = new NotificationDashboard();
        notificationDashboard.setTotalNotifications(totalNotifications);
        notificationDashboard.setSuccessRate(Math.round(successRate * 100.0));
        notificationDashboard.setErrorRate(Math.round(errorRate * 100.0));
        notificationDashboard.setReadRate(Math.round(readRate * 100.0));

        return notificationDashboard;
    }
}