package com.example.notification.service.implement;

import com.example.notification.common.enums.CampaignStatus;
import com.example.notification.common.enums.ChannelType;
import com.example.notification.common.enums.NotificationStatus;
import com.example.notification.common.enums.NotificationType;
import com.example.notification.common.exception.BusinessException;
import com.example.notification.dto.response.NotificationResponse;
import com.example.notification.entity.Campaign;
import com.example.notification.entity.GroupMember;
import com.example.notification.entity.Notification;
import com.example.notification.entity.Template;
import com.example.notification.entity.User;
import com.example.notification.mapper.NotificationMapper;
import com.example.notification.repository.CampaignRepository;
import com.example.notification.repository.GroupMemberRepository;
import com.example.notification.repository.NotificationRepository;
import com.example.notification.repository.UserPreferenceRepository;
import com.example.notification.repository.UserRepository;
import com.example.notification.service.NotificationService;
import com.example.notification.service.sender.NotificationSenderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImplement implements NotificationService {

    private final CampaignRepository campaignRepository ;
    private final GroupMemberRepository groupMemberRepository ;
    private final UserRepository userRepository ;
    private final NotificationRepository notificationRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final NotificationSenderFactory notificationSenderFactory ;
    private static final Pattern TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_.-]+)\\s*}}");

    private final NotificationMapper notificationMapper ;
    @Override
    public void processCampaign(String campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new BusinessException("Campaign not found"));

        Template template = campaign.getTemplate();
        if (template == null || Boolean.FALSE.equals(template.getIsActive())) {
            throw new BusinessException("Campaign template is not active");
        }

        List<User> recipients = switch (campaign.getTargetType()) {
            case USER -> List.of(campaign.getTargetUser());
            case GROUP -> groupMemberRepository
                    .findAllByGroup(campaign.getTargetGroup())
                    .stream()
                    .map(GroupMember::getUser)
                    .toList();
            case ALL -> userRepository.findAll();
            case CONDITION -> throw new UnsupportedOperationException(
                    "Condition target type is not implemented yet"
            );
        };

        NotificationType notificationType = template.getNotificationType();
        if (notificationType == null) {
            throw new BusinessException("Template notification type is required");
        }
        ChannelType channel = template.getChannel();

        List<Notification> notifications = recipients.stream()
                .filter(Objects::nonNull)
                .filter(user -> !Boolean.FALSE.equals(user.getIsActive()))
                .filter(user -> hasRecipientAddress(user, channel))
                .filter(user -> isPreferenceEnabled(user, notificationType, channel))
                .map(user -> buildNotification(campaign, template, user, notificationType, channel))
                .filter(notification -> !notificationRepository.existsByIdempotencyKey(notification.getIdempotencyKey()))
                .toList();

        notificationRepository.saveAll(notifications);

        for (Notification notification : notifications) {
            notificationSenderFactory
                    .getSender(notification.getChannel())
                    .send(notification);
        }
        switch (campaign.getScheduleType()) {

            case IMMEDIATE, ONCE -> {
                campaign.setStatus(CampaignStatus.COMPLETED);
            }

            case RECURRING -> {
                CronExpression cron =
                        CronExpression.parse(campaign.getCronExpression());

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
            ChannelType channel
    ) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setCampaign(campaign);
        notification.setNotificationType(notificationType);
        notification.setChannel(channel);
        notification.setIdempotencyKey(buildIdempotencyKey(campaign, user, channel));
        notification.setRecipientAddress(resolveRecipientAddress(user, channel));
        Map<String, String> variables = buildVariables(campaign, template, user);
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

    private Map<String, String> buildVariables(Campaign campaign, Template template, User user) {
        Map<String, String> variables = new HashMap<>();
        variables.put("user_id", nullToEmpty(user.getId()));
        variables.put("userId", nullToEmpty(user.getId()));
        variables.put("user_name", nullToEmpty(user.getFullName()));
        variables.put("fullName", nullToEmpty(user.getFullName()));
        variables.put("email", nullToEmpty(user.getEmail()));
        variables.put("phone", nullToEmpty(user.getPhone()));
        variables.put("campaign_id", nullToEmpty(campaign.getId()));
        variables.put("campaignId", nullToEmpty(campaign.getId()));
        variables.put("campaign_name", nullToEmpty(campaign.getName()));
        variables.put("campaignName", nullToEmpty(campaign.getName()));
        variables.put("template_code", nullToEmpty(template.getCode()));
        variables.put("templateCode", nullToEmpty(template.getCode()));
        variables.put("notification_type", template.getNotificationType().name());
        variables.put("channel", template.getChannel().name());
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
    public List<NotificationResponse> getByUser(String userId) {
        if (userId == null) {
            throw new BusinessException("userId null") ;
        }
        Optional<User> existUser = userRepository.findById(userId) ;
        if (existUser.isEmpty()) {
            throw new BusinessException("Not found user") ;
        }

        List<Notification> notifications = notificationRepository.findAllByUser(existUser.get()) ;
        return notificationMapper.toResponse(notifications) ;

    }
}
