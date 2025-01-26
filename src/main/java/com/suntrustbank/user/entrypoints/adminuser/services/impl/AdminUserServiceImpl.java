package com.suntrustbank.user.entrypoints.adminuser.services.impl;

import com.suntrustbank.user.core.configs.webclient.WebClientService;
import com.suntrustbank.user.core.dtos.BasePagedResponse;
import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.enums.BaseResponseMessage;
import com.suntrustbank.user.core.enums.BaseResponseStatus;
import com.suntrustbank.user.core.errorhandling.exceptions.AuthWebClientException;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.UUIDGenerator;
import com.suntrustbank.user.entrypoints.adminuser.dtos.*;
import com.suntrustbank.user.entrypoints.adminuser.repository.AdminUserRepository;
import com.suntrustbank.user.entrypoints.adminuser.repository.enums.AdminRole;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.AdminUser;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.Role;
import com.suntrustbank.user.entrypoints.adminuser.repository.specification.AdminUserSpecification;
import com.suntrustbank.user.entrypoints.adminuser.services.AdminUserPermissionService;
import com.suntrustbank.user.entrypoints.adminuser.services.AdminUserService;
import com.suntrustbank.user.entrypoints.adminuser.services.PermissionService;
import com.suntrustbank.user.entrypoints.adminuser.services.RoleService;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;
import com.suntrustbank.user.services.dtos.AuthAdminRequestDto;
import com.suntrustbank.user.services.dtos.AuthResponseDto;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final PermissionService permissionService;
    private final RoleService roleService;
    private final AdminUserPermissionService adminUserPermissionService;
    private final WebClientService<AuthAdminRequestDto, AuthResponseDto> authAdminWebClientService;


    @Transactional
    public BaseResponse create(CreateAdminUserRequest request) throws GenericErrorCodeException {
        if (adminUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw GenericErrorCodeException.badRequest("admin user with email '" +request.getEmail()+"' already exists");
        }

        Role role = roleService.get(request.getRoleReference()).orElseThrow(() -> {
            throw GenericErrorCodeException.badRequest("role with reference "+request.getRoleReference()+" doesn't exist");
        });

        AdminUser adminUser;
        try {

            adminUser = new AdminUser();
            BeanUtils.copyProperties(request, adminUser);
            adminUser.setReference(UUIDGenerator.generate());
            adminUser.setRole(role.getName());
            adminUser.setTeamLead(role.isTeamLead());
            adminUser.setStatus(Status.ACTIVE);
            if (StringUtils.isNotBlank(request.getImageBase64())) {
                adminUser.setProfilePhoto(request.getImageBase64());
            }
            if (StringUtils.isBlank(request.getCountryCode()) || StringUtils.isBlank(request.getPhoneNumber())) {
                adminUser.setCountryCode(null);
                adminUser.setPhoneNumber(null);
            }

            adminUserRepository.save(adminUser);

            authAdminWebClientService.request(AuthAdminRequestDto.builder()
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .build());
        } catch (AuthWebClientException e) {
            throw e;
        } catch (Exception e) {
            throw GenericErrorCodeException.badRequest("an error occurred while creating admin user");
        }

        return BaseResponse.success(AdminUserDto.toDto(adminUser), BaseResponseMessage.SUCCESSFUL);
    }

    public BasePagedResponse getAdminUsers(String email, String firstName, String lastName, AdminRole role, Boolean isTeamLead, Status status, int size, int page) throws GenericErrorCodeException {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminUser> adminUserPagination = adminUserRepository.findAll(AdminUserSpecification.filterBy(email, firstName, lastName, role, isTeamLead, status), pageable);

        return BasePagedResponse.builder()
            .data(adminUserPagination.getContent().stream().map(AdminUserDto::toDto).toList())
            .page(BasePagedResponse.PageData.builder()
                .page(adminUserPagination.getNumber()).size(adminUserPagination.getSize())
                .numberOfElements(adminUserPagination.getNumberOfElements()).totalElements((int) adminUserPagination.getTotalElements())
                .totalPages(adminUserPagination.getTotalPages()).build())
            .message(BaseResponseMessage.SUCCESSFUL).status(BaseResponseStatus.SUCCESS).build();
    }

    public BaseResponse getAdminUser(String adminUserReference) throws GenericErrorCodeException {
        AdminUser existingOrgUser = getAdminUserByReference(adminUserReference);

        return BaseResponse.success(AdminUserDto.toDto(existingOrgUser), BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse reassignRole(RoleReassignmentRequest request) throws GenericErrorCodeException {
        AdminRole adminRole;
        try {
            adminRole = AdminRole.valueOf(request.getRole());
        } catch (Exception e) {
            throw GenericErrorCodeException.badRequest("invalid role parsed");
        }

        AdminUser existingOrgUser = getAdminUserByReference(request.getReference());
        checkAndThrowIfStatusIsNotActive(existingOrgUser, "user is "+existingOrgUser.getStatus()+" role cannot be changed");

        if (existingOrgUser.getRole().equals(adminRole) && !existingOrgUser.isTeamLead()) {
            return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
        }

        existingOrgUser.setRole(adminRole);
        existingOrgUser.setTeamLead(false);
        updateAdminUser(existingOrgUser);
        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse addLeadStatus(AdminUserRequest request) throws GenericErrorCodeException {
        AdminUser existingOrgUser = getAdminUserByReference(request.getReference());
        checkAndThrowIfStatusIsNotActive(existingOrgUser, "user is "+existingOrgUser.getStatus()+" therefore cannot be made a team lead");

        if (existingOrgUser.isTeamLead()) {
            return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
        }

        existingOrgUser.setTeamLead(true);
        updateAdminUser(existingOrgUser);
        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse removeLeadStatus(AdminUserRequest request) throws GenericErrorCodeException {
        AdminUser existingOrgUser = getAdminUserByReference(request.getReference());
        checkAndThrowIfStatusIsDeleted(existingOrgUser, "user is DELETED therefore team lead status cannot be changed");

        if (!existingOrgUser.isTeamLead()) {
            return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
        }

        existingOrgUser.setTeamLead(false);
        updateAdminUser(existingOrgUser);
        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse updateStatus(UpdateStatusRequest request) throws GenericErrorCodeException {
        Status status;
        try {
            status = Status.valueOf(request.getStatus());
        } catch (Exception e) {
            throw GenericErrorCodeException.badRequest("invalid status parsed");
        }

        AdminUser existingOrgUser = getAdminUserByReference(request.getReference());
        checkAndThrowIfStatusIsDeleted(existingOrgUser, "user is DELETED therefore status cannot be changed");

        if (existingOrgUser.getStatus().equals(status)) {
            return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
        }

        existingOrgUser.setStatus(status);
        updateAdminUser(existingOrgUser);
        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse getRoles() throws GenericErrorCodeException {
        return BaseResponse.success(roleService.get(), BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse getPermissions() throws GenericErrorCodeException {
        return BaseResponse.success(permissionService.get(), BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse getAdminUserPermissions(String adminUserReference) throws GenericErrorCodeException {
        AdminUser adminUser = getAdminUserByReference(adminUserReference);

        AdminUserDto adminUserDto = AdminUserDto.toDto(adminUser);
        adminUserDto.setPermissions(adminUserPermissionService.getByReference(adminUserReference));

        return BaseResponse.success(adminUserDto, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse addPermission(PermissionRequest request) throws GenericErrorCodeException {
        AdminUser existingOrgUser = getAdminUserByReference(request.getReference());
        checkAndThrowIfStatusIsNotActive(existingOrgUser, "user is "+existingOrgUser.getStatus()+" permissions cannot be updated");

        var permission = permissionService.get(request.getPermissionReference()).orElseThrow(() -> {
            throw GenericErrorCodeException.badRequest("failed to find permission with reference " + request.getPermissionReference());
        });

        adminUserPermissionService.save(existingOrgUser, permission);

        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse removePermission(PermissionRequest request) throws GenericErrorCodeException {
        AdminUser existingOrgUser = getAdminUserByReference(request.getReference());
        checkAndThrowIfStatusIsNotActive(existingOrgUser, "user is "+existingOrgUser.getStatus()+" permissions cannot be updated");

        var permission = permissionService.get(request.getPermissionReference()).orElseThrow(() -> {
            throw GenericErrorCodeException.badRequest("failed to find permission with reference " + request.getPermissionReference());
        });

        adminUserPermissionService.remove(existingOrgUser, permission);

        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    private AdminUser getAdminUserByReference(String reference) throws GenericErrorCodeException {
        return adminUserRepository.findByReference(reference).orElseThrow(() -> {
            throw GenericErrorCodeException.notFound("admin user reference provided wasn't found");
        });
    }

    private void checkAndThrowIfStatusIsNotActive(AdminUser user, String errorMessage) throws GenericErrorCodeException {
        if (!user.getStatus().equals(Status.ACTIVE)) {
            throw GenericErrorCodeException.badRequest(errorMessage);
        }
    }

    private void checkAndThrowIfStatusIsDeleted(AdminUser user, String errorMessage) throws GenericErrorCodeException {
        if (user.getStatus().equals(Status.DELETED)) {
            throw GenericErrorCodeException.badRequest(errorMessage);
        }
    }

    private void updateAdminUser(AdminUser user) {
        user.setUpdatedAt(new Date());
        adminUserRepository.save(user);
    }
}
