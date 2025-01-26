package com.suntrustbank.user.entrypoints.adminuser.services;

import com.suntrustbank.user.entrypoints.adminuser.dtos.RoleDto;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<RoleDto> get();
    Optional<Role> get(String reference);
}
