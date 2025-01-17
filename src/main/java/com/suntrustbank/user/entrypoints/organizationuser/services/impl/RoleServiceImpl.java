package com.suntrustbank.user.entrypoints.organizationuser.services.impl;

import com.suntrustbank.user.entrypoints.organizationuser.dtos.RoleDto;
import com.suntrustbank.user.entrypoints.organizationuser.repository.RoleRepository;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.Role;
import com.suntrustbank.user.entrypoints.organizationuser.services.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public List<RoleDto> get() {
        List<Role> roleList = roleRepository.findAll();
        return roleList.stream()
                .map(role -> {
                    RoleDto dto = new RoleDto();
                    dto.setReference(role.getReference());
                    dto.setName(role.getName().name());
                    dto.setRoleType(role.getRoleType().name());
                    dto.setDescription(role.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Optional<Role> get(String reference) {
        return roleRepository.findByReference(reference);
    }
}
