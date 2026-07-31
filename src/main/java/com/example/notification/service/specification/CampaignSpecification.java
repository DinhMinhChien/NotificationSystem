package com.example.notification.service.specification;

import com.example.notification.entity.Campaign;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public class CampaignSpecification {
    public static Specification<Campaign> likeName(String keyword) {
        return new Specification<Campaign>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<Campaign> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (keyword == null) {
                    return criteriaBuilder.conjunction() ;
                }
                return criteriaBuilder.like(root.get("name"),"%" + keyword + "%") ;
            }
        };
    }
    public static Specification<Campaign> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }
}
