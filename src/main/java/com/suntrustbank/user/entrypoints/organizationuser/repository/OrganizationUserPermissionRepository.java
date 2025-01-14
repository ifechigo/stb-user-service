package com.suntrustbank.user.entrypoints.organizationuser.repository;

import com.suntrustbank.user.entrypoints.organizationuser.repository.models.OrganizationUser;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.OrganizationUserPermission;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationUserPermissionRepository extends JpaRepository<OrganizationUserPermission, Long> {
    List<OrganizationUserPermission> findAllByOrganizationUser_Reference(String reference);

    int deleteByOrganizationUserAndPermission(OrganizationUser organizationUser, Permission permission);

    //    String getAdminReference();
//    String getEmail();
//    Role getRole();
//    boolean getStatus();
//    String getPermissions();
//
//    @Query(value = """
//        SELECT
//            au.admin_user.reference AS adminReference,
//            au.email,
//            au.role,
//            au.status AS status,
//            STRING_AGG(p.name, ',') WITHIN GROUP (ORDER BY p.name) AS permissions
//        FROM admin_user_permissions aup
//        INNER JOIN admin_user au ON aup.admin_user_id = au.id
//        INNER JOIN permissions p ON aup.permission_id = p.id
//        WHERE au.email = :email
//        GROUP BY au.id, ao.role, o.organization_reference, o.name, a.user_id, a.email, ao.status, ao.active
//        """, nativeQuery = true)
//    OrganizationUserPermissionProjection findAccountDetailsByEmailAndOrganizationReference(@Param("email") String email);
//
//    @Modifying
//    @Transactional
//    @Query(value = """
//        INSERT INTO admin_user_permissions (admin_user_id, permission_id, enabled)
//        SELECT
//            :adminUserId,
//            p.id,
//            1 -- MSSQL uses BIT for boolean
//        FROM permissions p
//        WHERE p.reference IN (:permissionReferences)
//        """, nativeQuery = true)
//    void saveMemberPermissions(
//            @Param("adminUserId") Long adminUserId,
//            @Param("permissionReferences") Set<String> permissionReferences);
//
//    @Modifying
//    @Transactional
//    @Query(value = """
//        INSERT INTO admin_user_permissions (admin_user_id, permission_id, enabled)
//        SELECT
//            ao.id,
//            rp.permission_id
//        FROM role_permissions rp
//        INNER JOIN roles r ON rp.role_id = r.id
//        INNER JOIN organizations o ON o.organization_reference = :organizationReference
//        INNER JOIN account_organizations ao ON ao.organization_id = o.id
//        INNER JOIN admin_users a ON a.id = ao.account_id
//        WHERE
//            r.name = :role
//            AND a.email = :email
//        """, nativeQuery = true)
//    void saveMemberPermissionsByRole(
//            @Param("organizationReference") String organizationReference,
//            @Param("email") String email,
//            @Param("role") String role);
//
//    @Modifying
//    @Transactional
//    @Query(value = """
//        DELETE aup
//        FROM admin_user_permissions aup
//        INNER JOIN account_organizations ao ON aup.admin_user_id = ao.id
//        INNER JOIN admin_users a ON ao.account_id = a.id
//        INNER JOIN organizations o ON ao.organization_id = o.id
//        WHERE
//            o.organization_reference = :organizationReference
//            AND a.user_id IN (:userIds)
//        """, nativeQuery = true)
//    void deleteMemberPermissionsByUserIds(
//            @Param("organizationReference") String organizationReference,
//            @Param("userIds") Set<String> userIds);
//
//    @Modifying
//    @Transactional
//    @Query(value = """
//        INSERT INTO admin_user_permissions (admin_user_id, permission_id, enabled)
//        SELECT
//            ao.id,
//            rp.permission_id,
//            1 -- MSSQL uses BIT for boolean
//        FROM role_permissions rp
//        INNER JOIN roles r ON rp.role_id = r.id
//        INNER JOIN organizations o ON o.organization_reference = :organizationReference
//        INNER JOIN account_organizations ao ON ao.organization_id = o.id
//        INNER JOIN admin_users a ON a.id = ao.account_id
//        WHERE
//            r.name = :role
//            AND a.user_id IN (:userIds)
//        """, nativeQuery = true)
//    void insertMemberPermissionsByUserIds(
//            @Param("organizationReference") String organizationReference,
//            @Param("userIds") Set<String> userIds,
//            @Param("role") String role);
}
