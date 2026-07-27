package com.example.notification.controller;

import com.example.notification.common.BaseResponse;
import com.example.notification.dto.request.CampaignCreateRequest;
import com.example.notification.dto.response.CampaignListResponse;
import com.example.notification.dto.response.CampaignResponse;
import com.example.notification.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/campaigns")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService ;

    @PostMapping
    public ResponseEntity<BaseResponse<CampaignResponse>> create(@RequestBody CampaignCreateRequest request) {
        CampaignResponse response = campaignService.create(request) ;
        return ResponseEntity.ok(BaseResponse.ofSuccess(response,"Create campaign success")) ;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<CampaignListResponse>>> getAll(@RequestParam(required = false) String keyword) {
        List<CampaignListResponse> responses = campaignService.getAll(keyword);
        return ResponseEntity.ok(BaseResponse.ofSuccess(responses,"Get all campaign success")) ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CampaignResponse>> getDetail(@PathVariable String id) {
        CampaignResponse response = campaignService.getDetail(id);
        return ResponseEntity.ok(BaseResponse.ofSuccess(response,"Get detail campaign success")) ;
    }

}
