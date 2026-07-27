package com.example.notification.schedule;

import com.example.notification.common.enums.CampaignStatus;
import com.example.notification.entity.Campaign;
import com.example.notification.kafka.dto.NotificationEvent;
import com.example.notification.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class CampaignScheduler {
    private final CampaignRepository campaignRepository;
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void executeScheduledCampaigns() {

        List<Campaign> campaigns =
                campaignRepository.findAllByStatusAndScheduledAtLessThanEqual(
                        CampaignStatus.SCHEDULED,
                        LocalDateTime.now()
                );

        for (Campaign campaign : campaigns) {

            campaign.setStatus(CampaignStatus.RUNNING);

            kafkaTemplate.send(
                    "notification-topic",
                    new NotificationEvent(campaign.getId())
            );
        }

        campaignRepository.saveAll(campaigns);
    }
}
