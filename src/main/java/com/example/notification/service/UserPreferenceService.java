package com.example.notification.service;

import com.example.notification.dto.request.UpdatePreferenceRequest;
import com.example.notification.dto.response.UserPreferenceResponse;

import java.util.List;

public interface UserPreferenceService {
    List<UserPreferenceResponse> getPreferences(String userId) ;
    void update(String userId, UpdatePreferenceRequest request);
}
