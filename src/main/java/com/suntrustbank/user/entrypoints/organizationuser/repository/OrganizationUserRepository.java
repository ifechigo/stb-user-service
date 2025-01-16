package com.suntrustbank.user.entrypoints.organizationuser.repository;

import com.suntrustbank.user.entrypoints.organizationuser.repository.enums.OrganizationRole;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.OrganizationUser;
import com.suntrustbank.user.entrypoints.organizationuser.repository.specification.OrganizationUserSpecification;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, Long> {
    Optional<OrganizationUser> findByReference(String reference);
    Optional<OrganizationUser> findByEmail(String email);
    Page<OrganizationUser> findAll(Specification<OrganizationUser> organizationUserSpecification, Pageable pageable);
}
