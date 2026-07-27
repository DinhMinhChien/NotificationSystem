package com.example.notification.repository;

import com.example.notification.common.enums.CampaignStatus;
import com.example.notification.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign,String>, JpaSpecificationExecutor<Campaign> {
    List<Campaign> findAllByStatusAndScheduledAtLessThanEqual(
            CampaignStatus status,
            LocalDateTime time
    );

}
