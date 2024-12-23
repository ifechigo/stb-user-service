package com.suntrustbank.user.entrypoints;

import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.jwt.JwtUtil;
import com.suntrustbank.user.entrypoints.services.UserService;
import com.suntrustbank.user.entrypoints.dtos.BusinessUpdateRequest;
import com.suntrustbank.user.entrypoints.dtos.UserRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.suntrustbank.user.core.utils.jwt.JwtUtil.USER_NAME;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtService;

    @GetMapping("/validate/{phoneNumber}")
    public BaseResponse validatePhone(@PathVariable String phoneNumber) throws GenericErrorCodeException {
        return userService.validatePhoneNumberAndNotify(phoneNumber);
    }

    @PostMapping("/create")
    public BaseResponse createUserAccount(@RequestBody @Validated UserRequestDto requestDto) throws GenericErrorCodeException {
        return userService.createUser(requestDto);
    }

    @PostMapping("/business")
    public BaseResponse createBusiness(@RequestHeader("Authorization") String authorizationHeader, @RequestBody BusinessUpdateRequest requestDto) throws  GenericErrorCodeException {
        var userId = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        requestDto.setUserId(userId);
        return userService.createBusinessProfile(requestDto);
    }

    @GetMapping("/business")
    public BaseResponse getUserBusiness(@RequestHeader("Authorization") String authorizationHeader) throws  GenericErrorCodeException {
        var userId = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        return userService.getBusiness(userId);
    }
}
