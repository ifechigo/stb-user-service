package com.suntrustbank.user.entrypoints.user.repository;

import com.suntrustbank.user.entrypoints.user.repository.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByCreator_Reference(String creatorReference);
}
