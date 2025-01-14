package com.suntrustbank.user.entrypoints.organizationuser.services.impl;

import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.PermissionDto;
import com.suntrustbank.user.entrypoints.organizationuser.repository.OrganizationUserPermissionRepository;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.OrganizationUser;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.OrganizationUserPermission;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.Permission;
import com.suntrustbank.user.entrypoints.organizationuser.services.OrganizationUserPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationUserPermissionServiceImpl implements OrganizationUserPermissionService {

    private final OrganizationUserPermissionRepository organizationUserPermissionRepository;

    public List<PermissionDto> get(String organizationUserReference) throws GenericErrorCodeException {
        List<OrganizationUserPermission> permissionList = organizationUserPermissionRepository.findAllByOrganizationUser_Reference(organizationUserReference);
        return permissionList.stream()
                .map(organizationUserPermission -> {
                    PermissionDto dto = new PermissionDto();
                    dto.setReference(organizationUserPermission.getPermission().getReference());
                    dto.setName(organizationUserPermission.getPermission().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void save(OrganizationUser organizationUser, Permission permission) throws GenericErrorCodeException {
        OrganizationUserPermission  organizationUserPermission = new OrganizationUserPermission();
        organizationUserPermission.setEnabled(true);
        organizationUserPermission.setPermission(permission);
        organizationUserPermission.setOrganizationUser(organizationUser);
        organizationUserPermissionRepository.save(organizationUserPermission);
    }

    @Transactional
    @Modifying
    public int remove(OrganizationUser organizationUser, Permission permission) throws GenericErrorCodeException {
        return organizationUserPermissionRepository.deleteByOrganizationUserAndPermission(organizationUser, permission);
    }
}
