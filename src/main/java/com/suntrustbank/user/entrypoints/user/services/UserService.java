package com.suntrustbank.user.entrypoints.user.services;

import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.user.dtos.SignUpRequest;
import com.suntrustbank.user.entrypoints.user.dtos.UserRequestDto;
import com.suntrustbank.user.entrypoints.user.dtos.UserUpdateRequestDto;

public interface UserService {
    BaseResponse signUp(SignUpRequest request) throws GenericErrorCodeException;
    BaseResponse createUser(UserRequestDto requestDto) throws GenericErrorCodeException;
    BaseResponse updateUser(UserUpdateRequestDto requestDto) throws GenericErrorCodeException;

}
