package com.example.notification.service.specification;

import com.example.notification.common.enums.ChannelType;
import com.example.notification.entity.Template;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public class TemplateSpecification {
    public static Specification<Template> likeKeyword(String keyword) {
        return new Specification<Template>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<Template> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (keyword == null) {
                    return criteriaBuilder.conjunction() ;
                }
                return criteriaBuilder.like(root.get("code"),"%" + keyword + "%") ;
            }
        };
    }
    public static Specification<Template> equalChannel(ChannelType channel) {
        return new Specification<Template>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<Template> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (channel == null) {
                    return criteriaBuilder.conjunction() ;
                }
                return criteriaBuilder.equal(root.get("channel"),channel) ;
            }
        };
    }
    public static Specification<Template> equalLanguage(String language) {
        return new Specification<Template>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<Template> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (language == null) {
                    return criteriaBuilder.conjunction() ;
                }
                return criteriaBuilder.equal(root.get("language"),language) ;
            }
        };
    }
    public static Specification<Template> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }
}
