package com.suntrustbank.user.entrypoints.user;

import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.jwt.JwtUtil;
import com.suntrustbank.user.entrypoints.user.dtos.BusinessRequestDto;
import com.suntrustbank.user.entrypoints.user.dtos.BusinessUpdateRequestDto;
import com.suntrustbank.user.entrypoints.user.services.BusinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.suntrustbank.user.core.utils.jwt.JwtUtil.USER_NAME;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user/business")
public class BusinessController {

    private final BusinessService businessService;
    private final JwtUtil jwtService;


    @PostMapping
    public BaseResponse createBusiness(@RequestHeader("Authorization") String authorizationHeader, @RequestBody @Validated BusinessRequestDto requestDto) throws  GenericErrorCodeException {
        var userReference = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        requestDto.setUserReference(userReference);
        return businessService.createBusinessProfile(requestDto, authorizationHeader);
    }

    @PutMapping("/{businessReference}/update")
    public BaseResponse updateBusiness(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String businessReference,
        @RequestBody @Validated BusinessUpdateRequestDto requestDto) throws  GenericErrorCodeException {
        var userReference = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        requestDto.setUserReference(userReference);
        requestDto.setBusinessReference(businessReference);
        return businessService.updateBusinessProfile(requestDto);
    }

    @GetMapping
    public BaseResponse getUserBusiness(@RequestHeader("Authorization") String authorizationHeader) throws  GenericErrorCodeException {
        var userReference = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        return businessService.getBusiness(userReference);
    }
}
