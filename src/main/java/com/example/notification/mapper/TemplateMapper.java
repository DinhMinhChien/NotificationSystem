package com.example.notification.mapper;

import com.example.notification.dto.response.TemplateResponse;
import com.example.notification.entity.Template;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TemplateMapper {
    TemplateResponse toResponse(Template template) ;
    List<TemplateResponse> toResponse(List<Template> templates) ;
}
