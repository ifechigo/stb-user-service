package com.suntrustbank.user.entrypoints.adminuser.services;

import com.suntrustbank.user.core.dtos.BasePagedResponse;
import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.adminuser.dtos.*;
import com.suntrustbank.user.entrypoints.adminuser.repository.enums.AdminRole;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;

public interface AdminUserService {
    BaseResponse create(CreateAdminUserRequest request) throws GenericErrorCodeException;
    BasePagedResponse getAdminUsers(String email, String firstName, String lastName, AdminRole role, Boolean isTeamLead, Status status, int size, int page) throws GenericErrorCodeException;
    BaseResponse getAdminUser(String adminUserReference) throws GenericErrorCodeException;
    BaseResponse reassignRole(RoleReassignmentRequest request) throws GenericErrorCodeException;
    BaseResponse addLeadStatus(AdminUserRequest request) throws GenericErrorCodeException;
    BaseResponse removeLeadStatus(AdminUserRequest request) throws GenericErrorCodeException;
    BaseResponse updateStatus(UpdateStatusRequest request) throws GenericErrorCodeException;
    BaseResponse getRoles() throws GenericErrorCodeException;
    BaseResponse getPermissions() throws GenericErrorCodeException;
    BaseResponse getAdminUserPermissions(String adminUserReference) throws GenericErrorCodeException;
    BaseResponse addPermission(PermissionRequest request) throws GenericErrorCodeException;
    BaseResponse removePermission(PermissionRequest request) throws GenericErrorCodeException;
}
