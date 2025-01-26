package com.suntrustbank.user.entrypoints.adminuser.services;

import com.suntrustbank.user.entrypoints.adminuser.dtos.PermissionDto;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionService {
    List<PermissionDto> get();
    Optional<Permission> get(String reference);
}
