package com.suntrustbank.user.entrypoints.organizationuser;


import com.suntrustbank.user.core.dtos.BasePagedResponse;
import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.jwt.JwtUtil;
import com.suntrustbank.user.entrypoints.organizationuser.dtos.*;
import com.suntrustbank.user.entrypoints.organizationuser.repository.enums.OrganizationRole;
import com.suntrustbank.user.entrypoints.organizationuser.services.OrganizationUserService;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    public BasePagedResponse handleFetchingUsers(@RequestParam(required = false) String email, @RequestParam(required = false) String firstName,
         @RequestParam(required = false) String lastName, @RequestParam(required = false) OrganizationRole role, @RequestParam(required = false) Boolean isTeamLead,
         @RequestParam(required = false) Status status, @RequestParam(required = false, defaultValue = "10") int size, @RequestParam(required = false,
        defaultValue = "0") int page) throws GenericErrorCodeException {
        return organizationUserService.getOrganizationUsers(email, firstName, lastName, role, isTeamLead, status, size, page);
    }

    @GetMapping("/{organizationUserReference}")
    public BaseResponse handleFetchingUser(@PathVariable String organizationUserReference) throws GenericErrorCodeException {
        return organizationUserService.getOrganizationUser(organizationUserReference);
    }

    @PutMapping("/reassign-role")
    public BaseResponse updateUserAccount(@RequestBody @Validated RoleReassignmentRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.reassignRole(requestDto);
    }

    @PutMapping("/team-lead")
    public BaseResponse handleAssigningTeamLeadToUserAccount(@RequestBody @Validated OrganizationUserRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.addLeadStatus(requestDto);
    }

    @DeleteMapping("/team-lead")
    public BaseResponse handleRemovingTeamLeadFromUserAccount(@RequestBody @Validated OrganizationUserRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.removeLeadStatus(requestDto);
    }

    @PutMapping("/status")
    public BaseResponse updateUserAccount(@RequestBody @Validated UpdateStatusRequest requestDto) throws GenericErrorCodeException {
        return organizationUserService.updateStatus(requestDto);
    }

    @GetMapping("/roles")
    public BaseResponse handleFetchingRoles() throws GenericErrorCodeException {
        return organizationUserService.getRoles();
    }

    @GetMapping("/permissions")
    public BaseResponse handleFetchingPermissions() throws GenericErrorCodeException {
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

