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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TemplateServiceImplement implements TemplateService {
    private final TemplateRepository templateRepository ;
    private final TemplateMapper templateMapper ;

    @Override
    public TemplateResponse create(TemplateCreateRequest request) {

        String code = request.getCode() ;
        ChannelType channel = request.getChannel() ;
        String language = request.getLanguage() ;

        if (request.getNotificationType() == null) {
            throw new BusinessException("Notification type is required");
        }

        if (templateRepository.existsByCodeAndChannelAndLanguageAndDeletedFalse(code,channel,language)) {
            throw new BusinessException("Template is exist") ;
        }

        Template template = new Template() ;

        template.setCode(request.getCode());
        template.setChannel(request.getChannel());
        template.setNotificationType(request.getNotificationType());
        template.setLanguage(request.getLanguage());
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());
        template.setIsActive(request.getIsActive());

        Template templateSave = templateRepository.save(template) ;

        return templateMapper.toResponse(templateSave);

    }

    @Override
    public List<TemplateResponse> getAll(String keyword, ChannelType channel, String language) {
        Specification<Template> specification = Specification.unrestricted();
        specification = specification.and(TemplateSpecification.notDeleted()) ;
        if (keyword != null) {
            specification = specification.and(TemplateSpecification.likeKeyword(keyword)) ;
        }
        if (channel != null) {
            specification = specification.and(TemplateSpecification.likeChannel(channel)) ;
        }
        if (language != null) {
            specification = specification.and(TemplateSpecification.likeLanguage(language)) ;
        }
        List<Template> templates = templateRepository.findAll(specification) ;
        List<TemplateResponse> responses = templateMapper.toResponse(templates) ;
        return responses ;

    }

    @Override
    public void update(String id, TemplateUpdateRequest request) {
        Optional<Template> existTemplate = templateRepository.findById(id) ;

        if (existTemplate.isEmpty()) {
            throw new BusinessException("Not exist template !") ;
        }

        Template template = existTemplate.get() ;
        template.setCode(request.getCode());
        template.setChannel(request.getChannel());
        template.setNotificationType(request.getNotificationType());
        template.setLanguage(request.getLanguage());
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());
        template.setIsActive(request.getIsActive());

        templateRepository.save(template) ;
    }

    @Override
    public void delete(String id) {
        Optional<Template> existTemplate = templateRepository.findById(id) ;

        if (existTemplate.isEmpty()) {
            throw new BusinessException("Not exist template") ;
        }

        existTemplate.get().setDeleted(true);
        templateRepository.save(existTemplate.get()) ;
    }
}
