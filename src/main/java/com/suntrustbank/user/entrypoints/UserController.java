package com.suntrustbank.user.entrypoints;

import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.jwt.JwtUtil;
import com.suntrustbank.user.entrypoints.dtos.*;
import com.suntrustbank.user.entrypoints.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.suntrustbank.user.core.utils.jwt.JwtUtil.USER_NAME;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtService;

    @PostMapping("/signup")
    public BaseResponse validatePhone(@RequestBody SIgnUpRequest phoneNumber) throws GenericErrorCodeException {
        return userService.signUp(phoneNumber);
    }

    @PostMapping("/create")
    public BaseResponse createUserAccount(@RequestBody @Validated UserRequestDto requestDto) throws GenericErrorCodeException {
        return userService.createUser(requestDto);
    }

    @PutMapping("/update")
    public BaseResponse updateUserAccount(@RequestHeader("Authorization") String authorizationHeader, @RequestBody @Validated UserUpdateRequestDto requestDto) throws GenericErrorCodeException {
        var userId = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        requestDto.setUserId(userId);
        return userService.updateUser(requestDto);
    }

    @PostMapping("/business")
    public BaseResponse createBusiness(@RequestHeader("Authorization") String authorizationHeader, @RequestBody BusinessRequestDto requestDto) throws  GenericErrorCodeException {
        var userId = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        requestDto.setUserId(userId);
        return userService.createBusinessProfile(requestDto);
    }

    @PutMapping("/business/update")
    public BaseResponse updateBusiness(@RequestHeader("Authorization") String authorizationHeader, @RequestBody BusinessUpdateRequestDto requestDto) throws  GenericErrorCodeException {
        var userId = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        requestDto.setUserId(userId);
        return userService.updateBusinessProfile(requestDto);
    }

    @GetMapping("/business")
    public BaseResponse getUserBusiness(@RequestHeader("Authorization") String authorizationHeader) throws  GenericErrorCodeException {
        var userId = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        return userService.getBusiness(userId);
    }
}
