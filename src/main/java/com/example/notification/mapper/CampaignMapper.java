package com.example.notification.mapper;

import com.example.notification.dto.response.CampaignListResponse;
import com.example.notification.dto.response.CampaignResponse;
import com.example.notification.entity.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CampaignMapper {
    @Mapping(target = "templateId", source = "template.id")
    @Mapping(target = "templateCode", source = "template.code")

    @Mapping(target = "targetUserId", source = "targetUser.id")

    @Mapping(target = "targetGroupId", source = "targetGroup.id")
    @Mapping(target = "targetGroupName", source = "targetGroup.name")
    CampaignResponse toResponse(Campaign campaign);

    @Mapping(target = "templateCode", source = "template.code")
    List<CampaignListResponse> toResponse(List<Campaign> campaigns) ;
}
