package com.suntrustbank.user.entrypoints.organizationuser.services;

import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.CreateOrganizationUserRequest;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.PermissionRequest;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.RoleReassignmentRequest;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.UpdateStatusRequest;

public interface OrganizationUserService {
    BaseResponse create(CreateOrganizationUserRequest request) throws GenericErrorCodeException;
    BaseResponse reassignRole(RoleReassignmentRequest request) throws GenericErrorCodeException;
    BaseResponse updateStatus(UpdateStatusRequest request) throws GenericErrorCodeException;
    BaseResponse getPermissions() throws GenericErrorCodeException;
    BaseResponse getUserPermissions(String organizationUserReference) throws GenericErrorCodeException;
    BaseResponse addPermission(PermissionRequest request) throws GenericErrorCodeException;
    BaseResponse removePermission(PermissionRequest request) throws GenericErrorCodeException;
}
