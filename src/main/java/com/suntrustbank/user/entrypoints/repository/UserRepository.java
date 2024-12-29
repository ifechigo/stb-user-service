package com.suntrustbank.user.entrypoints.repository;

import com.suntrustbank.user.entrypoints.repository.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByReference(String reference);
}
