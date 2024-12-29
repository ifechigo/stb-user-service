package com.suntrustbank.user.entrypoints.services.impl;


import com.suntrustbank.user.core.configs.webclient.WebClientService;
import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.enums.BaseResponseMessage;
import com.suntrustbank.user.core.enums.ErrorCode;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.UUIDGenerator;
import com.suntrustbank.user.entrypoints.dtos.*;
import com.suntrustbank.user.entrypoints.repository.BusinessRepository;
import com.suntrustbank.user.entrypoints.repository.OrganizationRepository;
import com.suntrustbank.user.entrypoints.repository.models.*;
import com.suntrustbank.user.entrypoints.services.BusinessService;
import com.suntrustbank.user.entrypoints.services.CashPointService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final WebClientService<TransactionUserRequestDto, TransactionUserResponseDto> transactionWebClientService;
    private final CashPointService cashPointService;

    private final BusinessRepository businessRepository;
    private final OrganizationRepository organizationRepository;


    @Override
    @Transactional
    public BaseResponse createBusinessProfile(BusinessRequestDto requestDto, String authorizationHeader) throws GenericErrorCodeException {
        Organization organization = organizationRepository.findByCreator_Reference(requestDto.getUserId())
            .orElseThrow(() ->  GenericErrorCodeException.notFound("user not found"));

        log.info("number of businesses present [{}]", organization.getBusinesses().size());


        //Todo further clarification needed
//        if (StringUtils.isNotBlank(requestDto.getEmail()) && organizationRepository.isEmailTaken(requestDto.getEmail())) {
//            throw new GenericErrorCodeException("Email is already in use.", ErrorCode.BAD_REQUEST, HttpStatus.CONFLICT);
//        }
//        if (StringUtils.isNotBlank(requestDto.getPhoneNumber()) && organizationRepository.isPhoneNumberTaken(requestDto.getPhoneNumber())) {
//            throw new GenericErrorCodeException("Phone Number is already in use.", ErrorCode.BAD_REQUEST, HttpStatus.CONFLICT);
//        }

        Business business = new Business();
        business.setReference(UUIDGenerator.generate());
        business.setOrganization(organization);
        business.setName(requestDto.getName());
        business.setEmail(requestDto.getEmail());
        business.setAddress(requestDto.getAddress());
        business.setCountryCode(requestDto.getCountryCode());
        business.setPhoneNumber(requestDto.getPhoneNumber());
        business.setLogoImage(requestDto.getLogoImageBase64());
        business.setBusinessType(requestDto.getBusinessType());
        businessRepository.save(business);

        organization.addBusiness(business);
        organizationRepository.save(organization);

        CashPoint cashPoint = cashPointService.createCashPoint(business);

        TransactionUserResponseDto transactionUserResponseDto = transactionWebClientService.request(TransactionUserRequestDto.builder()
                .authorization(authorizationHeader)
                .creatorReference(organization.getCreator().getReference())
                .userReference(requestDto.getUserId()).userFullName(organization.getCreator().getLastName() +" "+organization.getCreator().getFirstName())
                .businessReference(business.getReference()).businessName(business.getName())
                .cashPointReference(cashPoint.getReference()).walletId(cashPoint.getWalletId())
                .build());
        if (!transactionUserResponseDto.getStatus().equals("SUCCESS")) {
            throw new GenericErrorCodeException(
                    transactionUserResponseDto.getMessage(),
                    ErrorCode.BAD_REQUEST,
                    HttpStatus.BAD_REQUEST
            );
        }
        return BaseResponse.success(business, BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    public BaseResponse updateBusinessProfile(BusinessUpdateRequestDto requestDto) throws GenericErrorCodeException {
        Optional<Business> existingBusiness = businessRepository.findByUserIdAndBusinessId(requestDto.getUserId(), requestDto.getBusinessId());
        if (existingBusiness.isEmpty()) {
            throw GenericErrorCodeException.notFound("user does not exist");
        }
        Business businessDetails = existingBusiness.get();

        if (StringUtils.isNotBlank(requestDto.getEmail())) {
            businessDetails.setEmail(requestDto.getEmail());
        }
        if (StringUtils.isNotBlank(requestDto.getCacNumber())) {
            businessDetails.setCacNumber(requestDto.getCacNumber());
        }
        if (StringUtils.isNotBlank(requestDto.getLogoImageBase64())) {
            businessDetails.setLogoImage(requestDto.getLogoImageBase64());
        }
        if (StringUtils.isNotBlank(requestDto.getCountryCode())) {
            businessDetails.setCountryCode(requestDto.getCountryCode());
        }
        if (StringUtils.isNotBlank(requestDto.getPhoneNumber())) {
            businessDetails.setPhoneNumber(requestDto.getPhoneNumber());
        }

        businessRepository.save(businessDetails);

        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    public BaseResponse getBusiness(String userId) throws GenericErrorCodeException {
        Organization organization = organizationRepository.findByCreator_Reference(userId)
            .orElseThrow(() ->  GenericErrorCodeException.notFound("user not found"));

        return BaseResponse.success(BusinessResponseDto.toDto(organization), BaseResponseMessage.SUCCESSFUL);
    }
}
