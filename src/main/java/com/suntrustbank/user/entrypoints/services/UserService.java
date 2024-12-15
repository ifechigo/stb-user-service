package com.suntrustbank.user.entrypoints.services;

import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.dtos.BusinessUpdateRequest;
import com.suntrustbank.user.entrypoints.dtos.UserRequestDto;
import com.suntrustbank.user.entrypoints.dtos.UserUpdateRequestDto;

public interface UserService {
    BaseResponse validatePhoneNumberAndNotify(String email) throws GenericErrorCodeException;
    BaseResponse createUser(UserRequestDto requestDto) throws GenericErrorCodeException;
    BaseResponse createBusinessProfile(BusinessUpdateRequest requestDto) throws GenericErrorCodeException;
    BaseResponse getBusiness(String organizationId) throws GenericErrorCodeException;

}
