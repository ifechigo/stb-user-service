package com.suntrustbank.user.entrypoints.organizationuser.services;

import com.suntrustbank.user.entrypoints.organizationuser.dtos.PermissionDto;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionService {
    List<PermissionDto> get();
    Optional<Permission> get(String reference);
}
