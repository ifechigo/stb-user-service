package com.suntrustbank.user.entrypoints.organizationuser;


import com.suntrustbank.user.core.aop.AuthorizedOrganizationUser;
import com.suntrustbank.user.core.dtos.BasePagedResponse;
import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.*;
import com.suntrustbank.user.entrypoints.organizationuser.repository.enums.OrganizationRole;
import com.suntrustbank.user.entrypoints.organizationuser.services.OrganizationUserService;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;
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

    @AuthorizedOrganizationUser(hasAuthority = "admin_management:create_admin")
    @PostMapping("/create")
    public BaseResponse createUserAccount(@RequestBody @Validated CreateOrganizationUserRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.create(requestDto);
    }

    @AuthorizedOrganizationUser(hasAuthority = "admin_management:read_admin")
    @GetMapping
    public BasePagedResponse handleFetchingUsers(@RequestParam(required = false) String email, @RequestParam(required = false) String firstName,
         @RequestParam(required = false) String lastName, @RequestParam(required = false) OrganizationRole role, @RequestParam(required = false) Boolean isTeamLead,
         @RequestParam(required = false) Status status, @RequestParam(required = false, defaultValue = "10") int size, @RequestParam(required = false,
        defaultValue = "0") int page) throws GenericErrorCodeException {
        return organizationUserService.getOrganizationUsers(email, firstName, lastName, role, isTeamLead, status, size, page);
    }

    @AuthorizedOrganizationUser(hasAuthority = "admin_management:read_admin")
    @GetMapping("/{organizationUserReference}")
    public BaseResponse handleFetchingUser(@PathVariable String organizationUserReference) throws GenericErrorCodeException {
        return organizationUserService.getOrganizationUser(organizationUserReference);
    }

    @AuthorizedOrganizationUser(hasAuthority = "admin_management:update_admin_role")
    @PutMapping("/reassign-role")
    public BaseResponse updateUserAccount(@RequestBody @Validated RoleReassignmentRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.reassignRole(requestDto);
    }

    @AuthorizedOrganizationUser(hasAuthority = "admin_management:create_team_lead")
    @PutMapping("/team-lead")
    public BaseResponse handleAssigningTeamLeadToUserAccount(@RequestBody @Validated OrganizationUserRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.addLeadStatus(requestDto);
    }

    @AuthorizedOrganizationUser(hasAuthority = "admin_management:delete_team_lead")
    @DeleteMapping("/team-lead")
    public BaseResponse handleRemovingTeamLeadFromUserAccount(@RequestBody @Validated OrganizationUserRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.removeLeadStatus(requestDto);
    }

    @AuthorizedOrganizationUser(hasAuthority = "admin_management:update_admin_status")
    @PutMapping("/status")
    public BaseResponse updateUserAccount(@RequestBody @Validated UpdateStatusRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.updateStatus(requestDto);
    }

    @AuthorizedOrganizationUser(hasAuthority = "admin_management:update_admin_role")
    @GetMapping("/roles")
    public BaseResponse handleFetchingRoles() throws GenericErrorCodeException {
        return organizationUserService.getRoles();
    }

    @AuthorizedOrganizationUser(hasAuthority = "admin_management:read_permissions")
    @GetMapping("/permissions")
    public BaseResponse handleFetchingPermissions() throws GenericErrorCodeException {
        return organizationUserService.getPermissions();
    }

    @AuthorizedOrganizationUser(hasAuthority = "admin_management:read_admin_permissions")
    @GetMapping("/{organizationUserReference}/permissions")
    public BaseResponse handleFetchingUserPermissions(@PathVariable String organizationUserReference) throws GenericErrorCodeException {
        return organizationUserService.getUserPermissions(organizationUserReference);
    }

    @AuthorizedOrganizationUser(hasAuthority = "admin_management:update_admin_permission")
    @PutMapping("/permissions")
    public BaseResponse handleAssigningUserPermissions(@RequestBody @Validated PermissionRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.addPermission(requestDto);
    }

    @AuthorizedOrganizationUser(hasAuthority = "admin_management:delete_admin_permission")
    @DeleteMapping("/permissions")
    public BaseResponse handleDeletingUserPermissions(@RequestBody @Validated PermissionRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.removePermission(requestDto);
    }
}

