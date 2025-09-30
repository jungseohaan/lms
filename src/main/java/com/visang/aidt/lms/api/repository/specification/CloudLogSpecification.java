package com.visang.aidt.lms.api.repository.specification;

import com.visang.aidt.lms.api.repository.entity.CloudLog;
import org.springframework.data.jpa.domain.Specification;

public class CloudLogSpecification {

    public static Specification<CloudLog> equal(String key, String val) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.equal(root.get(key), val);
    }

    public static Specification<CloudLog> leftRightLike(String key, String val) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get(key),"%" + val + "%");
    }

    public static Specification<CloudLog> greaterThanOrEqualTo(String key, String val) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.greaterThanOrEqualTo(root.get(key), val);
    }

    public static Specification<CloudLog> lessThanOrEqualTo(String key, String val) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.lessThanOrEqualTo(root.get(key), val);
    }
}
