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
@RequestMapping("/v1/admin")
public class AdminPermissionController {

    private final AdminUserService adminUserService;

    @AuthorizedAdminUser(hasAuthority = "admin_management:read_permissions")
    @GetMapping("/permissions")
    public BaseResponse handleFetchingPermissions() throws GenericErrorCodeException {
        return adminUserService.getPermissions();
    }
}

