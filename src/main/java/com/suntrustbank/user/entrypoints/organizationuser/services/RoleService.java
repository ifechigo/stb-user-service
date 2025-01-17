package com.suntrustbank.user.entrypoints.organizationuser.services;

import com.suntrustbank.user.entrypoints.organizationuser.dtos.PermissionDto;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.RoleDto;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.Permission;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<RoleDto> get();
    Optional<Role> get(String reference);
}
