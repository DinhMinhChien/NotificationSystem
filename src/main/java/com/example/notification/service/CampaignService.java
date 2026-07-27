package com.example.notification.service;

import com.example.notification.dto.request.CampaignCreateRequest;
import com.example.notification.dto.response.CampaignListResponse;
import com.example.notification.dto.response.CampaignResponse;

import java.util.List;

public interface CampaignService {
    CampaignResponse create(CampaignCreateRequest request) ;
    List<CampaignListResponse> getAll(String keyword) ;
    CampaignResponse getDetail(String id) ;

}
