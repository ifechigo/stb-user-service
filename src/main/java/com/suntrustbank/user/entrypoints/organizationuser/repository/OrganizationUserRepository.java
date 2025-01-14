package com.suntrustbank.user.entrypoints.organizationuser.repository;

import com.suntrustbank.user.entrypoints.organizationuser.repository.models.OrganizationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, Long> {
    Optional<OrganizationUser> findByReference(String reference);

//    @Transactional
//    @Modifying
//    @Query("""
//        UPDATE organization_users ou
//        SET ou.role = :role,
//            ou.updated_at = :updatedAt
//        WHERE ou.reference = :reference
//        """)
//    int updateRole( @Param("reference") String reference, @Param("role") Role role, @Param("updatedAt") Date updatedAt);
//
//    @Transactional
//    @Modifying
//    @Query("""
//        UPDATE organization_users ou
//        SET ou.status = :status,
//            ou.updated_at = :updatedAt
//        WHERE ou.reference = :reference
//        """)
//    int updateStatus( @Param("reference") String reference, @Param("status") Status status, @Param("updatedAt") Date updatedAt);
}
