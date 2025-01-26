package com.suntrustbank.user.entrypoints.adminuser.repository;

import com.suntrustbank.user.entrypoints.adminuser.repository.models.AdminUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByReference(String reference);
    Optional<AdminUser> findByEmail(String email);
    Page<AdminUser> findAll(Specification<AdminUser> adminUserSpecification, Pageable pageable);
}
