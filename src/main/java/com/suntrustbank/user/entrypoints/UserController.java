package com.suntrustbank.user.entrypoints;

import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.services.UserService;
import com.suntrustbank.user.entrypoints.dtos.BusinessUpdateRequest;
import com.suntrustbank.user.entrypoints.dtos.UserRequestDto;
import com.suntrustbank.user.entrypoints.dtos.UserUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/validate/{phoneNumber}")
    public BaseResponse validatePhone(@PathVariable String phoneNumber) throws GenericErrorCodeException {
        return userService.validatePhoneNumberAndNotify(phoneNumber);
    }

    @PostMapping("/create")
    public BaseResponse createUserAccount(@RequestBody @Validated UserRequestDto requestDto) throws GenericErrorCodeException {
        return userService.createUser(requestDto);
    }

    @PostMapping("/business")
    public BaseResponse createBusiness(@RequestBody BusinessUpdateRequest requestDto, String organizationId) throws  GenericErrorCodeException {
        return userService.createBusinessProfile(requestDto);
    }

    @GetMapping("/business/{organizationId}")
    public BaseResponse getUserBusiness(@PathVariable String organizationId) throws  GenericErrorCodeException {
        return userService.getBusiness(organizationId);
    }
}
