package com.suntrustbank.user.entrypoints.adminuser.services.impl;

import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.adminuser.dtos.AdminUserDto;
import com.suntrustbank.user.entrypoints.adminuser.dtos.PermissionDto;
import com.suntrustbank.user.entrypoints.adminuser.repository.AdminUserPermissionRepository;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.AdminUser;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.AdminUserPermission;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.Permission;
import com.suntrustbank.user.entrypoints.adminuser.services.AdminUserPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserPermissionServiceImpl implements AdminUserPermissionService {

    private final AdminUserPermissionRepository adminUserPermissionRepository;

    public List<PermissionDto> getByReference(String adminUserReference) throws GenericErrorCodeException {
        List<AdminUserPermission> permissionList = adminUserPermissionRepository.findAllByAdminUser_Reference(adminUserReference);
        return permissionList.stream()
                .map(adminUserPermission -> {
                    PermissionDto dto = new PermissionDto();
                    dto.setReference(adminUserPermission.getPermission().getReference());
                    dto.setName(adminUserPermission.getPermission().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Optional<AdminUserDto> getByEmail(String email) throws GenericErrorCodeException {
        List<AdminUserPermission> permissionList = adminUserPermissionRepository.findAllByAdminUser_Email(email);

        if (permissionList.isEmpty()) {
            return Optional.empty();
        }

        AdminUserDto adminUserDto = AdminUserDto.toDto(permissionList.getFirst().getAdminUser());
        adminUserDto.setPermissions(permissionList.stream()
            .map(adminUserPermission -> {
                PermissionDto dto = new PermissionDto();
                dto.setName(adminUserPermission.getPermission().getName());
                return dto;
            })
            .collect(Collectors.toList()));

        return Optional.of(adminUserDto);
    }

    @Override
    public void save(AdminUser adminUser, Permission permission) throws GenericErrorCodeException {
        AdminUserPermission adminUserPermission = new AdminUserPermission();
        adminUserPermission.setEnabled(true);
        adminUserPermission.setPermission(permission);
        adminUserPermission.setAdminUser(adminUser);
        adminUserPermissionRepository.save(adminUserPermission);
    }

    @Transactional
    @Modifying
    public int remove(AdminUser adminUser, Permission permission) throws GenericErrorCodeException {
        return adminUserPermissionRepository.deleteByAdminUserAndPermission(adminUser, permission);
    }
}
