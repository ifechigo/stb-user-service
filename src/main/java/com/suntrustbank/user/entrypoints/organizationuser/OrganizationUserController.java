package com.suntrustbank.user.entrypoints.organizationuser;


import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.jwt.JwtUtil;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.CreateOrganizationUserRequest;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.PermissionRequest;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.RoleReassignmentRequest;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.UpdateStatusRequest;
import com.suntrustbank.user.entrypoints.organizationuser.services.OrganizationUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/organization/user")
public class OrganizationUserController {

    private final OrganizationUserService organizationUserService;
    private final JwtUtil jwtService;

    @PostMapping("/create")
    public BaseResponse createUserAccount(@RequestBody @Validated CreateOrganizationUserRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.create(requestDto);
    }

    @PutMapping("/reassign-role")
    public BaseResponse updateUserAccount(@RequestHeader("Authorization") String authorizationHeader, @RequestBody @Validated RoleReassignmentRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.reassignRole(requestDto);
    }

    @PutMapping("/status")
    public BaseResponse updateUserAccount(@RequestHeader("Authorization") String authorizationHeader, @RequestBody @Validated UpdateStatusRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.updateStatus(requestDto);
    }

    @GetMapping("/permissions")
    public BaseResponse handleFetchingPermissions(@RequestHeader("Authorization") String authorizationHeader) throws GenericErrorCodeException {
        return organizationUserService.getPermissions();
    }

    @GetMapping("/{organizationUserReference}/permissions")
    public BaseResponse handleFetchingUserPermissions(@PathVariable String organizationUserReference) throws GenericErrorCodeException {
        return organizationUserService.getUserPermissions(organizationUserReference);
    }

    @PutMapping("/permissions")
    public BaseResponse handleAssigningUserPermissions(@RequestBody @Validated PermissionRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.addPermission(requestDto);
    }

    @DeleteMapping("/permissions")
    public BaseResponse handleDeletingUserPermissions(@RequestBody @Validated PermissionRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.removePermission(requestDto);
    }
}

