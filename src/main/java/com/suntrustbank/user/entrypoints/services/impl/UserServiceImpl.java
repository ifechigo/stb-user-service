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

import java.util.Date;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final WebClientService<AuthRequestDto, AuthResponseDto> authWebClientService;
    private final NotificationService notificationService;

    private final CacheService cacheService;
    private final UserRepository userRepository;
    private final OnboardingRepository onboardingRepository;
    private final OrganizationRepository organizationRepository;

    private final Environment environment;
    private final OtpDevConfig otpDevConfig;

    private static final int LOCK_CHECK_TTL = 3;


    @Override
    @Transactional
    public BaseResponse signUp(SignUpRequest request) throws GenericErrorCodeException {
        Onboarding onboarding = new Onboarding();

        Optional<Onboarding> existingRecord = onboardingRepository.findByPhoneNumber(request.getPhoneNumber());
        if (existingRecord.isEmpty()) {
            onboarding.setReference(UUIDGenerator.generate());
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
        cacheService.acquireLock(onboarding.getReference(), code, LOCK_CHECK_TTL);
        notificationService.sendSMS(SmsRequest.builder().build());
        return BaseResponse.success(OnboardingResponseDto.builder().reference(onboarding.getReference()).build(), BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    @Transactional
    public BaseResponse createUser(UserRequestDto requestDto) throws GenericErrorCodeException {
        String code = cacheService.get(requestDto.getReference());
        if (StringUtils.isBlank(code) || !code.equalsIgnoreCase(requestDto.getOtp())) {
            throw new GenericErrorCodeException("invalid otp", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        Optional<Onboarding> onboardingRecord = onboardingRepository.findByReference(requestDto.getReference());
        if (onboardingRecord.isEmpty()) {
            throw GenericErrorCodeException.serverError();
        }

        Onboarding onboarding = onboardingRecord.get();

        String userId = UUIDGenerator.generate();
        User user = new User();
        user.setReference(userId);
        user.setCountryCode(onboarding.getCountryCode());
        user.setPhoneNumber(onboarding.getPhoneNumber());
        user.setRole(Role.OWNER);
        userRepository.save(user);
        Organization organization = new Organization();
        organization.setReference(UUIDGenerator.generate());
        organization.setCreator(user);
        organizationRepository.save(organization);

        AuthResponseDto authResponseDto = authWebClientService.request(AuthRequestDto.builder().userId(userId).phoneNumber(user.getPhoneNumber())
            .pin(requestDto.getPin()).build());
        if (!authResponseDto.getStatus().equals("SUCCESS")) {
            throw new GenericErrorCodeException(
                    authResponseDto.getMessage(),
                    ErrorCode.BAD_REQUEST,
                    HttpStatus.BAD_REQUEST
            );
        }

        onboarding.setStatus(OnboardingStatus.PHONE_VERIFIED);
        onboarding.setUpdatedAt(new Date());
        onboardingRepository.save(onboarding);

        return BaseResponse.success(user, BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    public BaseResponse updateUser(UserUpdateRequestDto requestDto) throws GenericErrorCodeException {
        User existingUser = userRepository.findByReference(requestDto.getUserId()).orElseThrow(
                () ->  GenericErrorCodeException.notFound("user does not exist"));

        if (StringUtils.isNotBlank(requestDto.getFirstName())) {
            existingUser.setFirstName(requestDto.getFirstName());
        }
        if (StringUtils.isNotBlank(requestDto.getLastName())) {
            existingUser.setLastName(requestDto.getLastName());
        }
        if (StringUtils.isNotBlank(requestDto.getEmail())) {
            existingUser.setEmail(requestDto.getEmail());
        }
        if (StringUtils.isNotBlank(requestDto.getAddress())) {
            existingUser.setAddress(requestDto.getAddress());
        }
        if (StringUtils.isNotBlank(requestDto.getState())) {
            existingUser.setState(requestDto.getState());
        }
        if (StringUtils.isNotBlank(requestDto.getLga())) {
            existingUser.setLga(requestDto.getLga());
        }
        if (StringUtils.isNotBlank(requestDto.getAltCountryCode())) {
            existingUser.setAltCountryCode(requestDto.getAltCountryCode());
        }
        if (StringUtils.isNotBlank(requestDto.getAltPhoneNumber())) {
            existingUser.setAltPhoneNumber(requestDto.getAltPhoneNumber());
        }
        if (StringUtils.isNotBlank(requestDto.getDob())) {
            existingUser.setDob(requestDto.getDob());
        }
        if (StringUtils.isNotBlank(requestDto.getProfilePhotoBase64())) {
            existingUser.setProfilePhoto(requestDto.getProfilePhotoBase64());
        }

        userRepository.save(existingUser);

        return BaseResponse.success(null, BaseResponseMessage.SUCCESSFUL);
    }
}
