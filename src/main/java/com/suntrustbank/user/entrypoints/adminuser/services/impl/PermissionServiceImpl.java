package com.suntrustbank.user.entrypoints.adminuser.services.impl;

import com.suntrustbank.user.entrypoints.adminuser.dtos.PermissionDto;
import com.suntrustbank.user.entrypoints.adminuser.repository.PermissionRepository;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.Permission;
import com.suntrustbank.user.entrypoints.adminuser.services.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public List<PermissionDto> get() {
        List<Permission> permissionList = permissionRepository.findAll();
        return permissionList.stream()
                .map(permission -> {
                    PermissionDto dto = new PermissionDto();
                    dto.setReference(permission.getReference());
                    dto.setName(permission.getName());
                    dto.setCategory(permission.getCategory().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Optional<Permission> get(String reference) {
        return permissionRepository.findByReference(reference);
    }
}
