package com.example.notification.mapper;

import com.example.notification.dto.response.UserPreferenceResponse;
import com.example.notification.entity.UserPreference;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserPreferenceMapper {
    UserPreferenceResponse toResponse(UserPreference userPreference);
    List<UserPreferenceResponse> toResponse(List<UserPreference> userPreferences) ;
}
