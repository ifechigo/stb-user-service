package com.suntrustbank.user.entrypoints.services;

import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.dtos.BusinessUpdateRequest;
import com.suntrustbank.user.entrypoints.dtos.SIgnUpRequest;
import com.suntrustbank.user.entrypoints.dtos.UserRequestDto;

public interface UserService {
    BaseResponse signUp(SIgnUpRequest request) throws GenericErrorCodeException;
    BaseResponse createUser(UserRequestDto requestDto) throws GenericErrorCodeException;
    BaseResponse createBusinessProfile(BusinessUpdateRequest requestDto) throws GenericErrorCodeException;
    BaseResponse getBusiness(String userId) throws GenericErrorCodeException;

}
