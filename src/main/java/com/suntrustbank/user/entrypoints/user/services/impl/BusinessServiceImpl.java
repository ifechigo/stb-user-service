package com.suntrustbank.user.entrypoints.user.services.impl;


import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.enums.BaseResponseMessage;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.UUIDGenerator;
import com.suntrustbank.user.entrypoints.user.dtos.BusinessRequestDto;
import com.suntrustbank.user.entrypoints.user.dtos.BusinessResponseDto;
import com.suntrustbank.user.entrypoints.user.dtos.BusinessUpdateRequestDto;
import com.suntrustbank.user.entrypoints.user.repository.BusinessRepository;
import com.suntrustbank.user.entrypoints.user.repository.OrganizationRepository;
import com.suntrustbank.user.entrypoints.user.repository.models.Business;
import com.suntrustbank.user.entrypoints.user.repository.models.CashPoint;
import com.suntrustbank.user.entrypoints.user.repository.models.Organization;
import com.suntrustbank.user.entrypoints.user.services.BusinessService;
import com.suntrustbank.user.entrypoints.user.services.CashPointService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final CashPointService cashPointService;

    private final BusinessRepository businessRepository;
    private final OrganizationRepository organizationRepository;


    @Override
    @Transactional
    public BaseResponse createBusinessProfile(BusinessRequestDto requestDto, String authorizationHeader) throws GenericErrorCodeException {
        Organization organization = organizationRepository.findByCreator_Reference(requestDto.getUserReference())
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

        CashPoint cashPoint = cashPointService.createCashPoint(authorizationHeader, organization, business);

        CompletableFuture.runAsync(() -> cashPointService.createNGNCashPointWallet(cashPoint));

        return BaseResponse.success(business, BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    public BaseResponse updateBusinessProfile(BusinessUpdateRequestDto requestDto) throws GenericErrorCodeException {
        Optional<Business> existingBusiness = businessRepository.findByUserReferenceAndBusinessReference(requestDto.getUserReference(), requestDto.getBusinessReference());
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
                .orElseThrow(() -> GenericErrorCodeException.notFound("user not found"));

        for (Business business : organization.getBusinesses()) {
            for (CashPoint cashPoint : business.getCashPoints()) {
                if (StringUtils.isBlank(cashPoint.getWalletReference())) {
                    try {
                        CashPoint newCashPoint = cashPointService.createNGNCashPointWallet(cashPoint);
                        cashPoint.setWalletReference(newCashPoint.getWalletReference());
                    } catch (Exception e) {
                        log.error("BusinessServiceImpl.getBusiness Failed, wallet reference and call to generate one failed. Error [{}]", e.getMessage(), e);
                        throw GenericErrorCodeException.serverError();
                    }
                }
            }
        }

        return BaseResponse.success(BusinessResponseDto.toDto(organization), BaseResponseMessage.SUCCESSFUL);
    }
}
