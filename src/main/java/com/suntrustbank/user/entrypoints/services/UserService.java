package com.suntrustbank.user.entrypoints.services;

import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.dtos.*;

public interface UserService {
    BaseResponse signUp(SignUpRequest request) throws GenericErrorCodeException;
    BaseResponse createUser(UserRequestDto requestDto) throws GenericErrorCodeException;
    BaseResponse updateUser(UserUpdateRequestDto requestDto) throws GenericErrorCodeException;

}
