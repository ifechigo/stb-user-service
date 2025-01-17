package com.suntrustbank.user.entrypoints.organizationuser.services;

import com.suntrustbank.user.core.dtos.BasePagedResponse;
import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.*;
import com.suntrustbank.user.entrypoints.organizationuser.repository.enums.OrganizationRole;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;

public interface OrganizationUserService {
    BaseResponse create(CreateOrganizationUserRequest request) throws GenericErrorCodeException;
    BasePagedResponse getOrganizationUsers(String email, String firstName, String lastName, OrganizationRole role, Boolean isTeamLead, Status status, int size, int page) throws GenericErrorCodeException;
    BaseResponse getOrganizationUser(String organizationUserReference) throws GenericErrorCodeException;
    BaseResponse reassignRole(RoleReassignmentRequest request) throws GenericErrorCodeException;
    BaseResponse addLeadStatus(OrganizationUserRequest request) throws GenericErrorCodeException;
    BaseResponse removeLeadStatus(OrganizationUserRequest request) throws GenericErrorCodeException;
    BaseResponse updateStatus(UpdateStatusRequest request) throws GenericErrorCodeException;
    BaseResponse getRoles() throws GenericErrorCodeException;
    BaseResponse getPermissions() throws GenericErrorCodeException;
    BaseResponse getUserPermissions(String organizationUserReference) throws GenericErrorCodeException;
    BaseResponse addPermission(PermissionRequest request) throws GenericErrorCodeException;
    BaseResponse removePermission(PermissionRequest request) throws GenericErrorCodeException;
}
