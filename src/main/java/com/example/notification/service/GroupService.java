package com.example.notification.service;

import com.example.notification.dto.request.GroupCreateRequest;
import com.example.notification.dto.request.InsertMemberRequest;
import com.example.notification.dto.response.GroupResponse;
import com.example.notification.entity.User;

import java.util.List;

public interface GroupService {
    GroupResponse create(GroupCreateRequest request) ;
    List<GroupResponse> getAll() ;
    void insertMember(String id, InsertMemberRequest request) ;
    List<User> getMember(String id) ;
}
