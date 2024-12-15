package com.suntrustbank.user.entrypoints.services.impl;

import com.suntrustbank.user.core.configs.cache.CacheService;
import com.suntrustbank.user.core.configs.properties.OtpDevConfig;
import com.suntrustbank.user.core.configs.webclient.WebClientService;
import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.enums.BaseResponseMessage;
import com.suntrustbank.user.core.enums.ErrorCode;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.RandomNumberGenerator;
import com.suntrustbank.user.entrypoints.dtos.*;
import com.suntrustbank.user.entrypoints.repository.*;
import com.suntrustbank.user.entrypoints.repository.enums.OnboardingStatus;
import com.suntrustbank.user.entrypoints.repository.enums.Role;
import com.suntrustbank.user.entrypoints.repository.models.Business;
import com.suntrustbank.user.entrypoints.repository.models.Onboarding;
import com.suntrustbank.user.entrypoints.repository.models.Organization;
import com.suntrustbank.user.entrypoints.repository.models.User;
import com.suntrustbank.user.entrypoints.services.UserService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final WebClientService<AuthRequestDto, AuthResponseDto> webClientService;
    private final WebClientService<String, AuthResponseDto> notificationService;

    private final CacheService cacheService;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final OnboardingRepository onboardingRepository;
    private final OrganizationRepository organizationRepository;

    private final Environment environment;
    private final OtpDevConfig otpDevConfig;


    private static final int LOCK_CHECK_TTL = 3;
    public static final String PRODUCTION = "prod";
    public static final String STAGING = "staging";
    public static final int ONE_RECORD_UPDATED = 1;


    @Override
    public BaseResponse validatePhoneNumberAndNotify(String phoneNumber) throws GenericErrorCodeException {
        if (!phoneNumber.matches("\\d{11}")) {
            throw new GenericErrorCodeException("not a valid phone number", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        Onboarding onboarding = new Onboarding();

        Optional<Onboarding> existingRecord = onboardingRepository.findByPhoneNumber(phoneNumber);
        if (existingRecord.isPresent()) {
            if (existingRecord.get().getStatus().equals(OnboardingStatus.PHONE_VERIFIED)) {
                throw new GenericErrorCodeException("phone number already exists", ErrorCode.BAD_REQUEST, HttpStatus.CONFLICT);
            }
            onboarding = existingRecord.get();
        } else {
            onboarding.setId(UUID.randomUUID().toString());
            onboarding.setStatus(OnboardingStatus.AWAITING_PHONE_OTP);
            onboarding.setPhoneNumber(phoneNumber);
            onboardingRepository.save(onboarding);
        }

        String code;
        if (environment.acceptsProfiles(PRODUCTION, STAGING)) {
            code = RandomNumberGenerator.generate(6);
            notificationService.request(code);
        } else {
            code = otpDevConfig.getPhoneOtp();
        }
        cacheService.acquireLock(onboarding.getId(), code, LOCK_CHECK_TTL);

        return BaseResponse.success(OnboardingResponseDto.builder().reference(onboarding.getId()).build(), BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    @Transactional
    public BaseResponse createUser(UserRequestDto requestDto) throws GenericErrorCodeException {
        String code = cacheService.get(requestDto.getReference());
        if (StringUtils.isBlank(code) || !code.equalsIgnoreCase(requestDto.getOtp())) {
            throw new GenericErrorCodeException("invalid otp", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        user.setRole(Role.OWNER);
        userRepository.save(user);
        String organizationId = UUID.randomUUID().toString();
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setCreator(user);
        organizationRepository.save(organization);

        webClientService.request(AuthRequestDto.builder().organizationId(organizationId).phoneNumber(requestDto.getPhoneNumber())
            .pin(requestDto.getPin()).build());

        onboardingRepository.updateStatusById(requestDto.getReference(), OnboardingStatus.PHONE_VERIFIED);

        return BaseResponse.success(organization, BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    @Transactional
    public BaseResponse createBusinessProfile(BusinessUpdateRequest requestDto) throws GenericErrorCodeException {
        Organization organization = organizationRepository.findById(requestDto.getOrganizationId())
            .orElseThrow(() -> new  GenericErrorCodeException("user not found", ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND));

        if (StringUtils.isNotBlank(requestDto.getEmail()) && organizationRepository.isEmailTaken(requestDto.getEmail())) {
            throw new GenericErrorCodeException("Email is already in use.", ErrorCode.BAD_REQUEST, HttpStatus.CONFLICT);
        }
        if (StringUtils.isNotBlank(requestDto.getAlternativePhoneNumber()) && organizationRepository.isPhoneNumberTaken(requestDto.getAlternativePhoneNumber())) {
            throw new GenericErrorCodeException("Phone Number is already in use.", ErrorCode.BAD_REQUEST, HttpStatus.CONFLICT);
        }

        User user = organization.getCreator();
        user.setFullName(requestDto.getFullName());
        user.setEmail(requestDto.getEmail());
        user.setState(requestDto.getState());
        user.setLga(requestDto.getLga());
        user.setAltPhoneNumber(requestDto.getAlternativePhoneNumber());
        user.setPhoto(requestDto.getPhotoBase64());
        userRepository.save(user);

        Business business = new Business();
        business.setId(UUID.randomUUID().toString());
        business.setOrganization(organization);
        business.setAddress(requestDto.getBusinessAddress());
        business.setBusinessType(requestDto.getBusinessType());
//        business.setEmail(requestDto.getEmail());
//        business.setState(requestDto.getState());
//        business.setLga(requestDto.getState());
        businessRepository.save(business);

        organization.addBusiness(business);
        organizationRepository.save(organization);

        return BaseResponse.success(organization, BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    public BaseResponse getBusiness(String organizationId) throws GenericErrorCodeException {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new  GenericErrorCodeException("user not found", ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND));

        return BaseResponse.success(organization, BaseResponseMessage.SUCCESSFUL);
    }
}
