package com.suntrustbank.user.entrypoints.organizationuser.services.impl;

import com.suntrustbank.user.core.configs.webclient.WebClientService;
import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.enums.BaseResponseMessage;
import com.suntrustbank.user.core.errorhandling.exceptions.AuthWebClientException;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.UUIDGenerator;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.*;
import com.suntrustbank.user.entrypoints.organizationuser.repository.OrganizationUserRepository;
import com.suntrustbank.user.entrypoints.organizationuser.repository.enums.OrganizationRole;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.OrganizationUser;
import com.suntrustbank.user.entrypoints.organizationuser.services.OrganizationUserPermissionService;
import com.suntrustbank.user.entrypoints.organizationuser.services.OrganizationUserService;
import com.suntrustbank.user.entrypoints.organizationuser.services.PermissionService;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;
import com.suntrustbank.user.services.dtos.AuthOrganizationRequestDto;
import com.suntrustbank.user.services.dtos.AuthResponseDto;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationUserServiceImpl implements OrganizationUserService {

    private final OrganizationUserRepository organizationUserRepository;
    private final PermissionService permissionService;
    private final OrganizationUserPermissionService organizationUserPermissionService;
    private final WebClientService<AuthOrganizationRequestDto, AuthResponseDto> authOrganizationWebClientService;


    @Transactional
    public BaseResponse create(CreateOrganizationUserRequest request) throws GenericErrorCodeException {
        OrganizationRole organizationRole;
        try {
            organizationRole = OrganizationRole.valueOf(request.getRole());

            OrganizationUser organizationUser = new OrganizationUser();
            BeanUtils.copyProperties(request, organizationUser);
            organizationUser.setReference(UUIDGenerator.generate());
            organizationUser.setRole(organizationRole);
            organizationUser.setProfilePhoto(request.getImageBase64());
            organizationUser.setStatus(Status.ACTIVE);
            if (StringUtils.isNotBlank(request.getImageBase64())) {
                organizationUser.setProfilePhoto(request.getImageBase64());
            }

            organizationUserRepository.save(organizationUser);

            authOrganizationWebClientService.request(AuthOrganizationRequestDto.builder()
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .build());
        } catch (AuthWebClientException e) {
            throw e;
        } catch (Exception e) {
            throw GenericErrorCodeException.badRequest("invalid role parsed");
        }

        return BaseResponse.success(
                OrganizationUserDto.builder().email(request.getEmail()).firstName(request.getFirstName()).lastName(request.getLastName())
                    .role(organizationRole.name()).countryCode(request.getCountryCode()).phoneNumber(request.getPhoneNumber()),
            BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse reassignRole(RoleReassignmentRequest request) throws GenericErrorCodeException {
        OrganizationRole organizationRole;
        try {
            organizationRole = OrganizationRole.valueOf(request.getRole());
        } catch (Exception e) {
            throw GenericErrorCodeException.badRequest("invalid role parsed");
        }

        OrganizationUser existingOrgUser = organizationUserRepository.findByReference(request.getReference()).orElseThrow(() -> {
            throw GenericErrorCodeException.notFound("organization user reference provided wasn't found");
        });

        if (!existingOrgUser.getStatus().equals(Status.ACTIVE)) {
            throw GenericErrorCodeException.badRequest("you cannot change the role of a(an) " + existingOrgUser.getStatus() +" user");
        }

        if (existingOrgUser.getRole().equals(organizationRole)) {
            throw GenericErrorCodeException.badRequest("role parsed is already assigned to organization user");
        }

        existingOrgUser.setRole(organizationRole);
        existingOrgUser.setUpdatedAt(new Date());
        organizationUserRepository.save(existingOrgUser);
        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse updateStatus(UpdateStatusRequest request) throws GenericErrorCodeException {
        Status status;
        try {
            status = Status.valueOf(request.getStatus());
        } catch (Exception e) {
            throw GenericErrorCodeException.badRequest("invalid status parsed");
        }
        OrganizationUser existingOrgUser = organizationUserRepository.findByReference(request.getReference()).orElseThrow(() -> {
            throw GenericErrorCodeException.notFound("organization user reference provided wasn't found");
        });

        if (existingOrgUser.getStatus().equals(status)) {
            throw GenericErrorCodeException.badRequest("status parsed is already assigned to organization user");
        }

        existingOrgUser.setStatus(status);
        existingOrgUser.setUpdatedAt(new Date());
        organizationUserRepository.save(existingOrgUser);
        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse getPermissions() throws GenericErrorCodeException {
        return BaseResponse.success(permissionService.get(), BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse getUserPermissions(String organizationUserReference) throws GenericErrorCodeException {
        organizationUserRepository.findByReference(organizationUserReference).orElseThrow(() -> {
            throw GenericErrorCodeException.notFound("organization user reference provided wasn't found");
        });

        return BaseResponse.success(organizationUserPermissionService.get(organizationUserReference), BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse addPermission(PermissionRequest request) throws GenericErrorCodeException {
        var organizationUser = organizationUserRepository.findByReference(request.getReference()).orElseThrow(() -> {
            throw GenericErrorCodeException.notFound("organization user reference provided wasn't found");
        });

        var permission = permissionService.get(request.getPermissionReference()).orElseThrow(() -> {
            throw GenericErrorCodeException.badRequest("failed to find permission with reference " + request.getPermissionReference());
        });

        organizationUserPermissionService.save(organizationUser, permission);

        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse removePermission(PermissionRequest request) throws GenericErrorCodeException {
        var organizationUser = organizationUserRepository.findByReference(request.getReference()).orElseThrow(() -> {
            throw GenericErrorCodeException.notFound("organization user reference provided wasn't found");
        });

        var permission = permissionService.get(request.getPermissionReference()).orElseThrow(() -> {
            throw GenericErrorCodeException.badRequest("failed to find permission with reference " + request.getPermissionReference());
        });

        organizationUserPermissionService.remove(organizationUser, permission);

        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }
}
