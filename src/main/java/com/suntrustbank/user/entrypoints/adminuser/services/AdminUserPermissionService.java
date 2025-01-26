package com.suntrustbank.user.entrypoints.adminuser.services;

import com.suntrustbank.user.entrypoints.adminuser.dtos.AdminUserDto;
import com.suntrustbank.user.entrypoints.adminuser.dtos.PermissionDto;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.AdminUser;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.Permission;

import java.util.List;
import java.util.Optional;

public interface AdminUserPermissionService {
    List<PermissionDto> getByReference(String adminUserReference);
    Optional<AdminUserDto> getByEmail(String email);
    void save(AdminUser adminUser, Permission permission);
    int remove(AdminUser adminUser, Permission permission);
}
