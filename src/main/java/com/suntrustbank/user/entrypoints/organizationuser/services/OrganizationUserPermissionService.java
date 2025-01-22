package com.suntrustbank.user.entrypoints.organizationuser.services;

import com.suntrustbank.user.entrypoints.organizationuser.dtos.OrganizationUserDto;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.PermissionDto;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.OrganizationUser;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.Permission;

import java.util.List;
import java.util.Optional;

public interface OrganizationUserPermissionService {
    List<PermissionDto> getByReference(String organizationUserReference);
    Optional<OrganizationUserDto> getByEmail(String email);
    void save(OrganizationUser organizationUser, Permission permission);
    int remove(OrganizationUser organizationUser, Permission permission);
}
