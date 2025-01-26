package com.suntrustbank.user.core.aop;

import com.suntrustbank.user.core.enums.ErrorCode;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.Constants;
import com.suntrustbank.user.core.utils.jwt.JwtUtil;
import com.suntrustbank.user.entrypoints.adminuser.dtos.AdminUserDto;
import com.suntrustbank.user.entrypoints.adminuser.dtos.PermissionDto;
import com.suntrustbank.user.entrypoints.adminuser.services.AdminUserPermissionService;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizedAdminUserHandler {

    private final HttpServletRequest httpServletRequest;

    private final AdminUserPermissionService adminUserPermissionService;

    private static final String AUTH_HEADER = "Authorization";

    /**
     * {@code @AuthorizedAdminUser} this validates the Admin and Role of the Admin
     * accessing a particular endpoint
     *
     *
     * @param joinPoint
     * @param authorizedAdminUser
     * @throws GenericErrorCodeException if the Admin is not authorized
     */
    @Before("@annotation(authorizedAdminUser)")
    public void proceedingJoinPoint(JoinPoint joinPoint, AuthorizedAdminUser authorizedAdminUser) throws Throwable {

        String authToken = httpServletRequest.getHeader(AUTH_HEADER);
        if (!JwtUtil.isValidBearerToken(authToken)) {
            throw new GenericErrorCodeException(ErrorCode.UNAUTHORIZED);
        }

        Object[] objects = joinPoint.getArgs();
        //fetches the email from the Auth token
        String authUserEmail = (String) JwtUtil.getPrincipalPayload(authToken).get(Constants.EMAIL);

        AdminUserDto adminUserDto = adminUserPermissionService.getByEmail(authUserEmail).orElseThrow(() -> {
            log.debug("=== User with email '{}' doesn't exist or have any permission", authUserEmail);
            throw new GenericErrorCodeException(ErrorCode.UNAUTHORIZED);
        });

        if (!Status.ACTIVE.equals(Status.valueOf(adminUserDto.getStatus()))) {
            log.debug("=== Member with email '{}' accessing this endpoint has been '{}'", authUserEmail, adminUserDto.getStatus());
            throw new GenericErrorCodeException("Unauthorized - Contact Admin", ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        String fullAuthority = String.format("%s:%s:%s", Constants.COMPANY_NAME, Constants.ACCOUNT_SERVICE, authorizedAdminUser.hasAuthority());

        if (!hasClaim(adminUserDto.getPermissions(), fullAuthority)) {
            log.debug("=== No permission '{}'. Member with email '{}' and role '{}' isn't authorized to access this endpoint",
                authorizedAdminUser.hasAuthority(), authUserEmail, adminUserDto.getRole());
            throw new GenericErrorCodeException("You are not authorized to perform this action", ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean hasClaim(List<PermissionDto> permissionDto, String claim) {
        return permissionDto.stream().anyMatch(permission -> claim.equals(permission.getName()));
    }
}
