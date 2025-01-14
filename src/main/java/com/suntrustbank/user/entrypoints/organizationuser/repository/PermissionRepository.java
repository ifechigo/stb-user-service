package com.suntrustbank.user.entrypoints.organizationuser.repository;

import com.suntrustbank.user.entrypoints.organizationuser.repository.models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByReference(String reference);
}
