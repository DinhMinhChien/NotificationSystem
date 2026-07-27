package com.example.notification.controller;

import com.example.notification.common.BaseResponse;
import com.example.notification.dto.request.UpdatePreferenceRequest;
import com.example.notification.dto.response.UserPreferenceResponse;
import com.example.notification.service.UserPreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/preferences")
public class UserPreferenceController {

    private UserPreferenceService userPreferenceService ;

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<List<UserPreferenceResponse>>> getPreferences(@PathVariable String userId) {
        List<UserPreferenceResponse> responses = userPreferenceService.getPreferences(userId) ;
        return ResponseEntity.ok(BaseResponse.ofSuccess(responses,"get preferences by user success")) ;

    }
    @PutMapping("/{userId}")
    public ResponseEntity<BaseResponse<String>> update(@PathVariable String userId, @RequestBody UpdatePreferenceRequest request) {
        userPreferenceService.update(userId,request);
        return ResponseEntity.ok(BaseResponse.ofSuccess("Update preferences success")) ;
    }
}
