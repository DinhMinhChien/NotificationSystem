package com.example.notification.service.implement;

import com.example.notification.common.enums.ChannelType;
import com.example.notification.common.exception.BusinessException;
import com.example.notification.dto.request.TemplateCreateRequest;
import com.example.notification.dto.request.TemplateUpdateRequest;
import com.example.notification.dto.response.TemplateResponse;
import com.example.notification.entity.Template;
import com.example.notification.mapper.TemplateMapper;
import com.example.notification.repository.TemplateRepository;
import com.example.notification.service.TemplateService;
import com.example.notification.service.specification.TemplateSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TemplateServiceImplement implements TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;

    @Override
    public TemplateResponse create(TemplateCreateRequest request) {
        if (request.getNotificationType() == null) {
            throw new BusinessException("Notification type is required");
        }

        if (templateRepository.existsByCodeAndChannelAndLanguageAndDeletedFalse(
                request.getCode(), request.getChannel(), request.getLanguage())) {
            throw new BusinessException("Template already exists");
        }

        Template template = new Template();
        template.setCode(request.getCode());
        template.setChannel(request.getChannel());
        template.setNotificationType(request.getNotificationType());
        template.setLanguage(request.getLanguage());
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());
        template.setIsActive(request.getIsActive());

        return templateMapper.toResponse(templateRepository.save(template));
    }

    @Override
    public List<TemplateResponse> getAll(String keyword, ChannelType channel, String language) {
        Specification<Template> specification = Specification.unrestricted();
        specification = specification.and(TemplateSpecification.notDeleted());

        if (keyword != null && !keyword.isBlank()) {
            specification = specification.and(TemplateSpecification.likeKeyword(keyword));
        }
        if (channel != null) {
            specification = specification.and(TemplateSpecification.equalChannel(channel));
        }
        if (language != null && !language.isBlank()) {
            specification = specification.and(TemplateSpecification.equalLanguage(language));
        }

        return templateMapper.toResponse(templateRepository.findAll(specification));
    }

    @Override
    public void update(String id, TemplateUpdateRequest request) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Not exist template"));

        // ✅ Check trùng lặp với các template khác khi sửa thông tin
        boolean exists = templateRepository.existsByCodeAndChannelAndLanguageAndDeletedFalse(
                request.getCode(), request.getChannel(), request.getLanguage());

        boolean isSelf = template.getCode().equals(request.getCode()) &&
                template.getChannel() == request.getChannel() &&
                template.getLanguage().equals(request.getLanguage());

        if (exists && !isSelf) {
            throw new BusinessException("Another template with same code, channel, and language already exists");
        }

        template.setCode(request.getCode());
        template.setChannel(request.getChannel());
        template.setNotificationType(request.getNotificationType());
        template.setLanguage(request.getLanguage());
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());
        template.setIsActive(request.getIsActive());

        templateRepository.save(template);
    }

    @Override
    public void delete(String id) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Not exist template"));

        template.setDeleted(true);
        templateRepository.save(template);
    }
}