package com.suntrustbank.user.entrypoints.services.impl;

import com.suntrustbank.user.core.configs.cache.CacheService;
import com.suntrustbank.user.core.configs.properties.OtpDevConfig;
import com.suntrustbank.user.core.configs.webclient.WebClientService;
import com.suntrustbank.user.core.dtos.BaseResponse;
import com.suntrustbank.user.core.dtos.SmsRequest;
import com.suntrustbank.user.core.enums.BaseResponseMessage;
import com.suntrustbank.user.core.enums.ErrorCode;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.RandomNumberGenerator;
import com.suntrustbank.user.core.utils.UUIDGenerator;
import com.suntrustbank.user.entrypoints.dtos.*;
import com.suntrustbank.user.entrypoints.repository.*;
import com.suntrustbank.user.entrypoints.repository.enums.OnboardingStatus;
import com.suntrustbank.user.entrypoints.repository.enums.Role;
import com.suntrustbank.user.entrypoints.repository.models.*;
import com.suntrustbank.user.entrypoints.services.UserService;
import com.suntrustbank.user.services.NotificationService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final WebClientService<AuthRequestDto, AuthResponseDto> webClientService;
    private final NotificationService notificationService;

    private final CacheService cacheService;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final CashPointRepository cashPointRepository;
    private final OnboardingRepository onboardingRepository;
    private final OrganizationRepository organizationRepository;

    private final Environment environment;
    private final OtpDevConfig otpDevConfig;


    private static final int LOCK_CHECK_TTL = 3;
    public static final String PRODUCTION = "prod";
    public static final String STAGING = "staging";


    @Override
    @Transactional
    public BaseResponse signUp(SIgnUpRequest request) throws GenericErrorCodeException {

        Onboarding onboarding = new Onboarding();

        Optional<Onboarding> existingRecord = onboardingRepository.findByPhoneNumber(request.getPhoneNumber());
        if (existingRecord.isEmpty()) {
            onboarding.setId(UUIDGenerator.generate());
            onboarding.setStatus(OnboardingStatus.AWAITING_PHONE_OTP);
            onboarding.setCountryCode(request.getCountryCode());
            onboarding.setPhoneNumber(request.getPhoneNumber());
            onboardingRepository.save(onboarding);
        } else {
            if (existingRecord.get().getStatus().equals(OnboardingStatus.AWAITING_PHONE_OTP)) {
                onboarding = existingRecord.get();
            } else {
                throw new GenericErrorCodeException("phone number already exists", ErrorCode.BAD_REQUEST, HttpStatus.CONFLICT);
            }
        }

        String code;
        if (!environment.acceptsProfiles("dev")) {
            code = RandomNumberGenerator.generate(6);
        } else {
            code = otpDevConfig.getPhoneOtp();
        }
        cacheService.acquireLock(onboarding.getId(), code, LOCK_CHECK_TTL);
        notificationService.sendSMS(SmsRequest.builder().build());
        return BaseResponse.success(OnboardingResponseDto.builder().reference(onboarding.getId()).build(), BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    @Transactional
    public BaseResponse createUser(UserRequestDto requestDto) throws GenericErrorCodeException {
        String code = cacheService.get(requestDto.getReference());
        if (StringUtils.isBlank(code) || !code.equalsIgnoreCase(requestDto.getOtp())) {
            throw new GenericErrorCodeException("invalid otp", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        String userId = UUIDGenerator.generate();
        User user = new User();
        user.setId(userId);
        user.setPhoneNumber(onboardingRepository.findById(requestDto.getReference()).get().getPhoneNumber());
        user.setRole(Role.OWNER);
        userRepository.save(user);
        Organization organization = new Organization();
        organization.setId(UUIDGenerator.generate());
        organization.setCreator(user);
        organizationRepository.save(organization);

        AuthResponseDto authResponseDto = webClientService.request(AuthRequestDto.builder().userId(userId).phoneNumber(user.getPhoneNumber())
            .pin(requestDto.getPin()).build());
        if (!authResponseDto.getStatus().equals("SUCCESS")) {
            throw new GenericErrorCodeException(
                    authResponseDto.getMessage(),
                    ErrorCode.BAD_REQUEST,
                    HttpStatus.BAD_REQUEST
            );
        }

        onboardingRepository.updateStatusById(requestDto.getReference(), OnboardingStatus.PHONE_VERIFIED);

        return BaseResponse.success(organization, BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    @Transactional
    public BaseResponse createBusinessProfile(BusinessUpdateRequest requestDto) throws GenericErrorCodeException {
        Organization organization = organizationRepository.findByCreatorId(requestDto.getUserId())
            .orElseThrow(() -> new  GenericErrorCodeException("user not found", ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND));

        if (organization.getBusinesses().isEmpty()) {

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
        }

        Business business = new Business();
        business.setId(UUIDGenerator.generate());
        business.setOrganization(organization);
        business.setAddress(requestDto.getBusinessAddress());
        business.setBusinessType(requestDto.getBusinessType());
        businessRepository.save(business);

        CashPoint cashPoint = new CashPoint();
        cashPoint.setId(UUIDGenerator.generate());
        cashPoint.setBusiness(business);
        cashPoint.setMain(true);
        cashPoint.setActive(true);

        String reference;
        do {
            reference = RandomNumberGenerator.generateAlphanumericCode(12);
        } while (cashPointRepository.findByReference(reference).isPresent());
        cashPoint.setReference(reference);
        cashPointRepository.save(cashPoint);

        organization.addBusiness(business);
        organizationRepository.save(organization);
        return BaseResponse.success(organization, BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    public BaseResponse getBusiness(String userId) throws GenericErrorCodeException {
        Organization organization = organizationRepository.findByCreatorId(userId)
            .orElseThrow(() -> new  GenericErrorCodeException("user not found", ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND));
        return BaseResponse.success(organization, BaseResponseMessage.SUCCESSFUL);
    }
}
