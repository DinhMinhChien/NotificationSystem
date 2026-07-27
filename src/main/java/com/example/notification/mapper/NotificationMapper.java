package com.example.notification.mapper;

import com.example.notification.dto.response.NotificationResponse;
import com.example.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "fullName",source = "user.fullName")
    List<NotificationResponse> toResponse(List<Notification> notification) ;
}
