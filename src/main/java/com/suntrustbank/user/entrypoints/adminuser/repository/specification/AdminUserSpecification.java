package com.suntrustbank.user.entrypoints.adminuser.repository.specification;

import com.suntrustbank.user.entrypoints.adminuser.repository.enums.AdminRole;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.AdminUser;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class AdminUserSpecification {

    public static Specification<AdminUser> filterBy(
        String email,
        String firstName,
        String lastName,
        AdminRole role,
        Boolean isTeamLead,
        Status status
    ) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(email)) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
            }

            if (isTeamLead != null) {
                predicates.add(criteriaBuilder.equal(root.get("isTeamLead"), isTeamLead));
            }

            if (role != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }

            if (StringUtils.isNotBlank(firstName)) {
                predicates.add(criteriaBuilder.like(root.get("firstName"), "%" + firstName + "%"));
            }

            if (StringUtils.isNotBlank(lastName)) {
                predicates.add(criteriaBuilder.like(root.get("lastName"), "%" + lastName + "%"));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
