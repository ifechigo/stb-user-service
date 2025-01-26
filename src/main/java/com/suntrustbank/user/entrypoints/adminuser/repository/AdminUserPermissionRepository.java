package com.suntrustbank.user.entrypoints.adminuser.repository;

import com.suntrustbank.user.entrypoints.adminuser.repository.models.AdminUser;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.AdminUserPermission;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminUserPermissionRepository extends JpaRepository<AdminUserPermission, Long> {
    List<AdminUserPermission> findAllByAdminUser_Reference(String reference);
    List<AdminUserPermission> findAllByAdminUser_Email(String email);
    int deleteByAdminUserAndPermission(AdminUser adminUser, Permission permission);
}
