package com.suntrustbank.user.entrypoints.user.services;

import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.user.dtos.BusinessRequestDto;
import com.suntrustbank.user.entrypoints.user.dtos.BusinessUpdateRequestDto;

public interface BusinessService {
    BaseResponse createBusinessProfile(BusinessRequestDto requestDto, String authorizationHeader) throws GenericErrorCodeException;
    BaseResponse updateBusinessProfile(BusinessUpdateRequestDto requestDto) throws GenericErrorCodeException;
    BaseResponse getBusiness(String userId) throws GenericErrorCodeException;

}
