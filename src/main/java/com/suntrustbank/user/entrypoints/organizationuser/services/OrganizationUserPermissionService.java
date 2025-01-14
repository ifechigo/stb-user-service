package com.suntrustbank.user.entrypoints.organizationuser.services;

import com.suntrustbank.user.entrypoints.organizationuser.dtos.PermissionDto;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.OrganizationUser;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.Permission;

import java.util.List;

public interface OrganizationUserPermissionService {
    List<PermissionDto> get(String organizationUserReference);
    void save(OrganizationUser organizationUser, Permission permission);
    int remove(OrganizationUser organizationUser, Permission permission);
}
