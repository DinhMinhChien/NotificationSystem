package com.example.notification.repository;

import com.example.notification.common.enums.ChannelType;
import com.example.notification.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TemplateRepository extends JpaRepository<Template,String>, JpaSpecificationExecutor<Template> {
    boolean existsByCodeAndChannelAndLanguageAndDeletedFalse(String code, ChannelType channel, String language );
}
