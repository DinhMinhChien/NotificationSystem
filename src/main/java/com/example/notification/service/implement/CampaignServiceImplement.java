package com.example.notification.service.implement;

import com.example.notification.common.enums.CampaignStatus;
import com.example.notification.common.exception.BusinessException;
import com.example.notification.dto.request.CampaignCreateRequest;
import com.example.notification.dto.response.CampaignListResponse;
import com.example.notification.dto.response.CampaignResponse;
import com.example.notification.entity.Campaign;
import com.example.notification.entity.Group;
import com.example.notification.entity.Template;
import com.example.notification.entity.User;
import com.example.notification.kafka.dto.NotificationEvent;
import com.example.notification.mapper.CampaignMapper;
import com.example.notification.repository.CampaignRepository;
import com.example.notification.repository.GroupRepository;
import com.example.notification.repository.TemplateRepository;
import com.example.notification.repository.UserRepository;
import com.example.notification.service.CampaignService;
import com.example.notification.service.specification.CampaignSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignServiceImplement implements CampaignService {

    private final TemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final TransactionTemplate transactionTemplate;

    @Override
    public CampaignResponse create(CampaignCreateRequest request) {

        // 1. Thực thi lưu DB bên trong Transaction riêng
        Campaign savedCampaign = transactionTemplate.execute(status -> {
            Template template = templateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new BusinessException("Template not exist"));

            User targetUser = null;
            Group targetGroup = null;

            switch (request.getTargetType()) {
                case ALL -> {}
                case USER -> targetUser = userRepository.findById(request.getTargetUserId())
                        .orElseThrow(() -> new BusinessException("User not exist"));
                case GROUP -> targetGroup = groupRepository.findById(request.getTargetGroupId())
                        .orElseThrow(() -> new BusinessException("Group not exist"));
                case CONDITION -> throw new BusinessException("Condition target type is not implemented yet");
                default -> throw new BusinessException("Unsupported target type");
            }

            Campaign campaign = new Campaign();
            campaign.setName(request.getName());
            campaign.setTemplate(template);
            campaign.setTargetType(request.getTargetType());
            campaign.setTargetUser(targetUser);
            campaign.setTargetGroup(targetGroup);
            campaign.setConditionExpression(request.getConditionExpression());
            campaign.setScheduleType(request.getScheduleType());

            switch (request.getScheduleType()) {
                case IMMEDIATE -> campaign.setStatus(CampaignStatus.RUNNING);

                case ONCE -> {
                    if (request.getScheduledAt() == null) {
                        throw new BusinessException("Scheduled time is required");
                    }
                    campaign.setStatus(CampaignStatus.SCHEDULED);
                    campaign.setScheduledAt(request.getScheduledAt());
                }

                case RECURRING -> {
                    if (request.getCronExpression() == null || request.getCronExpression().isBlank()) {
                        throw new BusinessException("Cron expression is required");
                    }
                    try {
                        // ✅ Bọc catch exception chuẩn hóa response
                        CronExpression cron = CronExpression.parse(request.getCronExpression());
                        campaign.setCronExpression(request.getCronExpression());
                        campaign.setScheduledAt(cron.next(LocalDateTime.now()));
                        campaign.setStatus(CampaignStatus.SCHEDULED);
                    } catch (IllegalArgumentException e) {
                        throw new BusinessException("Invalid cron expression format: " + request.getCronExpression());
                    }
                }
            }

            return campaignRepository.save(campaign);
        });

        // 2. Sau khi DB đã COMMIT thành công, mới gửi event sang Kafka (Tránh Side-effect)
        if (savedCampaign != null && savedCampaign.getScheduleType() == com.example.notification.common.enums.ScheduleType.IMMEDIATE) {
            try {
                kafkaTemplate.send("notification-topic", new NotificationEvent(savedCampaign.getId()));
            } catch (Exception e) {
                log.error("Failed to publish IMMEDIATE campaign event to Kafka: {}", savedCampaign.getId(), e);
            }
        }

        return campaignMapper.toResponse(savedCampaign);
    }

    @Override
    public List<CampaignListResponse> getAll(String keyword) {
        Specification<Campaign> specification = Specification.unrestricted();
        specification = specification.and(CampaignSpecification.notDeleted());
        if (keyword != null && !keyword.isBlank()) {
            specification = specification.and(CampaignSpecification.likeName(keyword));
        }
        return campaignMapper.toResponse(campaignRepository.findAll(specification));
    }

    @Override
    public CampaignResponse getDetail(String id) {
        if (id == null || id.isBlank()) {
            throw new BusinessException("Id null or empty");
        }
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Campaign not exist"));
        return campaignMapper.toResponse(campaign);
    }
}