package com.suntrustbank.user.entrypoints.adminuser.repository;

import com.suntrustbank.user.entrypoints.adminuser.repository.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByReference(String reference);
}
