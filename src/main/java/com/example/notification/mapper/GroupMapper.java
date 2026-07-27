package com.example.notification.mapper;

import com.example.notification.dto.response.GroupResponse;
import com.example.notification.entity.Group;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    GroupResponse toResponse(Group group) ;
    List<GroupResponse> toResponse(List<Group> groups) ;
}
