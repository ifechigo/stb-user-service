package com.suntrustbank.user.entrypoints.organizationuser.services.impl;

import com.suntrustbank.user.core.configs.webclient.WebClientService;
import com.suntrustbank.user.core.dtos.BasePagedResponse;
import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.enums.BaseResponseMessage;
import com.suntrustbank.user.core.enums.BaseResponseStatus;
import com.suntrustbank.user.core.errorhandling.exceptions.AuthWebClientException;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.UUIDGenerator;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.*;
import com.suntrustbank.user.entrypoints.organizationuser.repository.OrganizationUserRepository;
import com.suntrustbank.user.entrypoints.organizationuser.repository.enums.OrganizationRole;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.OrganizationUser;
import com.suntrustbank.user.entrypoints.organizationuser.repository.specification.OrganizationUserSpecification;
import com.suntrustbank.user.entrypoints.organizationuser.services.OrganizationUserPermissionService;
import com.suntrustbank.user.entrypoints.organizationuser.services.OrganizationUserService;
import com.suntrustbank.user.entrypoints.organizationuser.services.PermissionService;
import com.suntrustbank.user.entrypoints.user.dtos.BusinessResponseDto;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;
import com.suntrustbank.user.services.dtos.AuthOrganizationRequestDto;
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
import java.util.List;

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
        if (organizationUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw GenericErrorCodeException.badRequest("organization user with email '" +request.getEmail()+"' already exists");
        }

        OrganizationRole organizationRole;
        OrganizationUser organizationUser;
        try {
            organizationRole = OrganizationRole.valueOf(request.getRole());

            organizationUser = new OrganizationUser();
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
            OrganizationUserDto.builder().reference(organizationUser.getReference()).email(request.getEmail()).firstName(request.getFirstName()).lastName(request.getLastName())
            .role(organizationRole.name()).countryCode(request.getCountryCode()).phoneNumber(request.getPhoneNumber()).build(),
        BaseResponseMessage.SUCCESSFUL);
    }

    public BasePagedResponse getOrganizationUsers(String email, String firstName, String lastName, OrganizationRole role, Boolean isTeamLead, Status status, int size, int page) throws GenericErrorCodeException {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrganizationUser> organizationUserPage = organizationUserRepository.findAll(OrganizationUserSpecification.filterBy(email, firstName, lastName, role, isTeamLead, status), pageable);

        return BasePagedResponse.builder()
            .data(organizationUserPage.getContent().stream().map(organizationUser -> OrganizationUserDto.builder().email(organizationUser.getEmail())
                .firstName(organizationUser.getFirstName()).reference(organizationUser.getReference()).lastName(organizationUser.getLastName())
                .role(organizationUser.getRole().name()).countryCode(organizationUser.getCountryCode()).phoneNumber(organizationUser.getPhoneNumber())
                .isTeamLead(organizationUser.isTeamLead()).build()).toList())
            .page(BasePagedResponse.PageData.builder()
                .page(organizationUserPage.getNumber()).size(organizationUserPage.getSize())
                .numberOfElements(organizationUserPage.getNumberOfElements()).totalElements((int) organizationUserPage.getTotalElements())
                .totalPages(organizationUserPage.getTotalPages()).build())
            .message(BaseResponseMessage.SUCCESSFUL).status(BaseResponseStatus.SUCCESS).build();
    }

    public BaseResponse getOrganizationUser(String organizationUserReference) throws GenericErrorCodeException {
        OrganizationUser existingOrgUser = getOrganizationUserByReference(organizationUserReference);

        return BaseResponse.success(
            OrganizationUserDto.builder().email(existingOrgUser.getEmail()).firstName(existingOrgUser.getFirstName()).lastName(existingOrgUser.getLastName())
                .role(existingOrgUser.getRole().name()).countryCode(existingOrgUser.getCountryCode()).phoneNumber(existingOrgUser.getPhoneNumber())
                .isTeamLead(existingOrgUser.isTeamLead()).build(),
            BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse reassignRole(RoleReassignmentRequest request) throws GenericErrorCodeException {
        OrganizationRole organizationRole;
        try {
            organizationRole = OrganizationRole.valueOf(request.getRole());
        } catch (Exception e) {
            throw GenericErrorCodeException.badRequest("invalid role parsed");
        }

        OrganizationUser existingOrgUser = getOrganizationUserByReference(request.getReference());
        checkAndThrowIfStatusIsNotActive(existingOrgUser, "user is "+existingOrgUser.getStatus()+" role cannot be changed");

        if (existingOrgUser.getRole().equals(organizationRole) && !existingOrgUser.isTeamLead()) {
            return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
        }

        existingOrgUser.setRole(organizationRole);
        existingOrgUser.setTeamLead(false);
        updateOrganizationUser(existingOrgUser);
        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse addLeadStatus(OrganizationUserRequest request) throws GenericErrorCodeException {
        OrganizationUser existingOrgUser = getOrganizationUserByReference(request.getReference());
        checkAndThrowIfStatusIsNotActive(existingOrgUser, "user is "+existingOrgUser.getStatus()+" therefore cannot be made a team lead");

        if (existingOrgUser.isTeamLead()) {
            return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
        }

        existingOrgUser.setTeamLead(true);
        updateOrganizationUser(existingOrgUser);
        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse removeLeadStatus(OrganizationUserRequest request) throws GenericErrorCodeException {
        OrganizationUser existingOrgUser = getOrganizationUserByReference(request.getReference());
        checkAndThrowIfStatusIsDeleted(existingOrgUser, "user is DELETED therefore team lead status cannot be changed");

        if (!existingOrgUser.isTeamLead()) {
            return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
        }

        existingOrgUser.setTeamLead(false);
        updateOrganizationUser(existingOrgUser);
        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse updateStatus(UpdateStatusRequest request) throws GenericErrorCodeException {
        Status status;
        try {
            status = Status.valueOf(request.getStatus());
        } catch (Exception e) {
            throw GenericErrorCodeException.badRequest("invalid status parsed");
        }

        OrganizationUser existingOrgUser = getOrganizationUserByReference(request.getReference());
        checkAndThrowIfStatusIsDeleted(existingOrgUser, "user is DELETED therefore status cannot be changed");

        if (existingOrgUser.getStatus().equals(status)) {
            return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
        }

        existingOrgUser.setStatus(status);
        updateOrganizationUser(existingOrgUser);
        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse getPermissions() throws GenericErrorCodeException {
        return BaseResponse.success(permissionService.get(), BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse getUserPermissions(String organizationUserReference) throws GenericErrorCodeException {
        OrganizationUser organizationUser = getOrganizationUserByReference(organizationUserReference);

        OrganizationUserDto organizationUserDto = OrganizationUserDto.builder().reference(organizationUser.getReference()).email(organizationUser.getEmail())
            .firstName(organizationUser.getFirstName()).lastName(organizationUser.getLastName()).role(organizationUser.getRole().name())
            .isTeamLead(organizationUser.isTeamLead()).countryCode(organizationUser.getCountryCode()).phoneNumber(organizationUser.getPhoneNumber()).build();

        organizationUserDto.setPermissions(organizationUserPermissionService.get(organizationUserReference));

        return BaseResponse.success(organizationUserDto, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse addPermission(PermissionRequest request) throws GenericErrorCodeException {
        OrganizationUser existingOrgUser = getOrganizationUserByReference(request.getReference());
        checkAndThrowIfStatusIsNotActive(existingOrgUser, "user is "+existingOrgUser.getStatus()+" permissions cannot be updated");

        var permission = permissionService.get(request.getPermissionReference()).orElseThrow(() -> {
            throw GenericErrorCodeException.badRequest("failed to find permission with reference " + request.getPermissionReference());
        });

        organizationUserPermissionService.save(existingOrgUser, permission);

        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse removePermission(PermissionRequest request) throws GenericErrorCodeException {
        OrganizationUser existingOrgUser = getOrganizationUserByReference(request.getReference());
        checkAndThrowIfStatusIsNotActive(existingOrgUser, "user is "+existingOrgUser.getStatus()+" permissions cannot be updated");

        var permission = permissionService.get(request.getPermissionReference()).orElseThrow(() -> {
            throw GenericErrorCodeException.badRequest("failed to find permission with reference " + request.getPermissionReference());
        });

        organizationUserPermissionService.remove(existingOrgUser, permission);

        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    private OrganizationUser getOrganizationUserByReference(String reference) throws GenericErrorCodeException {
        return organizationUserRepository.findByReference(reference).orElseThrow(() -> {
            throw GenericErrorCodeException.notFound("organization user reference provided wasn't found");
        });
    }

    private void checkAndThrowIfStatusIsNotActive(OrganizationUser user, String errorMessage) throws GenericErrorCodeException {
        if (!user.getStatus().equals(Status.ACTIVE)) {
            throw GenericErrorCodeException.badRequest(errorMessage);
        }
    }

    private void checkAndThrowIfStatusIsDeleted(OrganizationUser user, String errorMessage) throws GenericErrorCodeException {
        if (user.getStatus().equals(Status.DELETED)) {
            throw GenericErrorCodeException.badRequest(errorMessage);
        }
    }

    private void updateOrganizationUser(OrganizationUser user) {
        user.setUpdatedAt(new Date());
        organizationUserRepository.save(user);
    }
}
