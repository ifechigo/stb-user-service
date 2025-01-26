package com.suntrustbank.user.entrypoints.adminuser;


import com.suntrustbank.user.core.aop.AuthorizedAdminUser;
import com.suntrustbank.user.core.dtos.BasePagedResponse;
import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.adminuser.dtos.*;
import com.suntrustbank.user.entrypoints.adminuser.repository.enums.AdminRole;
import com.suntrustbank.user.entrypoints.adminuser.services.AdminUserService;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/user")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @AuthorizedAdminUser(hasAuthority = "admin_management:create_admin")
    @PostMapping("/create")
    public BaseResponse createUserAccount(@RequestBody @Validated CreateAdminUserRequest requestDto) throws GenericErrorCodeException {
        return adminUserService.create(requestDto);
    }

    @AuthorizedAdminUser(hasAuthority = "admin_management:read_admin")
    @GetMapping
    public BasePagedResponse handleFetchingUsers(@RequestParam(required = false) String email, @RequestParam(required = false) String firstName,
                                                 @RequestParam(required = false) String lastName, @RequestParam(required = false) AdminRole role, @RequestParam(required = false) Boolean isTeamLead,
                                                 @RequestParam(required = false) Status status, @RequestParam(required = false, defaultValue = "10") int size, @RequestParam(required = false,
        defaultValue = "0") int page) throws GenericErrorCodeException {
        return adminUserService.getAdminUsers(email, firstName, lastName, role, isTeamLead, status, size, page);
    }

    @AuthorizedAdminUser(hasAuthority = "admin_management:read_admin")
    @GetMapping("/{adminUserReference}")
    public BaseResponse handleFetchingUser(@PathVariable String adminUserReference) throws GenericErrorCodeException {
        return adminUserService.getAdminUser(adminUserReference);
    }

    @AuthorizedAdminUser(hasAuthority = "admin_management:update_admin_role")
    @PutMapping("/reassign-role")
    public BaseResponse updateUserAccount(@RequestBody @Validated RoleReassignmentRequest requestDto) throws GenericErrorCodeException {
        return adminUserService.reassignRole(requestDto);
    }

    @AuthorizedAdminUser(hasAuthority = "admin_management:create_team_lead")
    @PutMapping("/team-lead")
    public BaseResponse handleAssigningTeamLeadToUserAccount(@RequestBody @Validated AdminUserRequest requestDto) throws GenericErrorCodeException {
        return adminUserService.addLeadStatus(requestDto);
    }

    @AuthorizedAdminUser(hasAuthority = "admin_management:delete_team_lead")
    @DeleteMapping("/team-lead")
    public BaseResponse handleRemovingTeamLeadFromUserAccount(@RequestBody @Validated AdminUserRequest requestDto) throws GenericErrorCodeException {
        return adminUserService.removeLeadStatus(requestDto);
    }

    @AuthorizedAdminUser(hasAuthority = "admin_management:update_admin_status")
    @PutMapping("/status")
    public BaseResponse updateUserAccount(@RequestBody @Validated UpdateStatusRequest requestDto) throws GenericErrorCodeException {
        return adminUserService.updateStatus(requestDto);
    }

    @AuthorizedAdminUser(hasAuthority = "admin_management:update_admin_role")
    @GetMapping("/roles")
    public BaseResponse handleFetchingRoles() throws GenericErrorCodeException {
        return adminUserService.getRoles();
    }

    @AuthorizedAdminUser(hasAuthority = "admin_management:read_permissions")
    @GetMapping("/permissions")
    public BaseResponse handleFetchingPermissions() throws GenericErrorCodeException {
        return adminUserService.getPermissions();
    }

    @AuthorizedAdminUser(hasAuthority = "admin_management:read_admin_permissions")
    @GetMapping("/{adminUserReference}/permissions")
    public BaseResponse handleFetchingUserPermissions(@PathVariable String adminUserReference) throws GenericErrorCodeException {
        return adminUserService.getAdminUserPermissions(adminUserReference);
    }

    @AuthorizedAdminUser(hasAuthority = "admin_management:update_admin_permission")
    @PutMapping("/permissions")
    public BaseResponse handleAssigningUserPermissions(@RequestBody @Validated PermissionRequest requestDto) throws GenericErrorCodeException {
        return adminUserService.addPermission(requestDto);
    }

    @AuthorizedAdminUser(hasAuthority = "admin_management:delete_admin_permission")
    @DeleteMapping("/permissions")
    public BaseResponse handleDeletingUserPermissions(@RequestBody @Validated PermissionRequest requestDto) throws GenericErrorCodeException {
        return adminUserService.removePermission(requestDto);
    }
}

