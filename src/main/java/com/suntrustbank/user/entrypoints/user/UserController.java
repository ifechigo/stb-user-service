package com.suntrustbank.user.entrypoints.user;

import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.jwt.JwtUtil;
import com.suntrustbank.user.entrypoints.user.dtos.SignUpRequest;
import com.suntrustbank.user.entrypoints.user.dtos.UserRequestDto;
import com.suntrustbank.user.entrypoints.user.dtos.UserUpdateRequestDto;
import com.suntrustbank.user.entrypoints.user.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.suntrustbank.user.core.utils.Constants.USER_NAME;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public BaseResponse validatePhone(@RequestBody @Validated SignUpRequest phoneNumber) throws GenericErrorCodeException {
        return userService.signUp(phoneNumber);
    }

    @PostMapping
    public BaseResponse createUserAccount(@RequestBody @Validated UserRequestDto requestDto) throws GenericErrorCodeException {
        return userService.createUser(requestDto);
    }

    @PutMapping
    public BaseResponse updateUserAccount(@RequestHeader("Authorization") String authorizationHeader, @RequestBody @Validated UserUpdateRequestDto requestDto) throws GenericErrorCodeException {
        var userId = (String) JwtUtil.getClaim(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unauthenticated);
        requestDto.setUserId(userId);
        return userService.updateUser(requestDto);
    }
}
