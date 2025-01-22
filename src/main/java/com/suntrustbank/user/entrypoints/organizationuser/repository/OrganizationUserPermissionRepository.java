package com.suntrustbank.user.entrypoints.organizationuser.repository;

import com.suntrustbank.user.entrypoints.organizationuser.repository.models.OrganizationUser;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.OrganizationUserPermission;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationUserPermissionRepository extends JpaRepository<OrganizationUserPermission, Long> {
    List<OrganizationUserPermission> findAllByOrganizationUser_Reference(String reference);
    List<OrganizationUserPermission> findAllByOrganizationUser_Email(String email);
    int deleteByOrganizationUserAndPermission(OrganizationUser organizationUser, Permission permission);
}
