package com.example.notification.service;

import com.example.notification.common.enums.ChannelType;
import com.example.notification.dto.request.TemplateCreateRequest;
import com.example.notification.dto.request.TemplateUpdateRequest;
import com.example.notification.dto.response.TemplateResponse;
import com.example.notification.entity.Template;

import java.util.List;

public interface TemplateService {
    TemplateResponse create(TemplateCreateRequest request) ;
    List<TemplateResponse> getAll(String keyword, ChannelType channel,String language) ;
    void update(String id, TemplateUpdateRequest request) ;
    void delete(String id) ;
}
