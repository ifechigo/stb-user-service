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
import com.suntrustbank.user.entrypoints.repository.enums.Status;
import com.suntrustbank.user.entrypoints.repository.models.*;
import com.suntrustbank.user.entrypoints.services.CashPointService;
import com.suntrustbank.user.entrypoints.services.UserService;
import com.suntrustbank.user.services.NotificationService;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.Email;
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
    private final CashPointService cashPointService;

    private final CacheService cacheService;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final OnboardingRepository onboardingRepository;
    private final OrganizationRepository organizationRepository;

    private final Environment environment;
    private final OtpDevConfig otpDevConfig;

    private static final int LOCK_CHECK_TTL = 3;


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
    public BaseResponse updateUser(UserUpdateRequestDto requestDto) throws GenericErrorCodeException {

        Optional<User> existingUser = userRepository.findById(requestDto.getUserId());
        if (existingUser.isEmpty()) {
            throw GenericErrorCodeException.notFound("user does not exist");
        }
        User userDetails = existingUser.get();

        if (StringUtils.isNotBlank(requestDto.getFirstName())) {
            userDetails.setFirstName(requestDto.getFirstName());
        }
        if (StringUtils.isNotBlank(requestDto.getLastName())) {
            userDetails.setLastName(requestDto.getLastName());
        }
        if (StringUtils.isNotBlank(requestDto.getEmail())) {
            userDetails.setEmail(requestDto.getEmail());
        }
        if (StringUtils.isNotBlank(requestDto.getAddress())) {
            userDetails.setAddress(requestDto.getAddress());
        }
        if (StringUtils.isNotBlank(requestDto.getState())) {
            userDetails.setState(requestDto.getState());
        }
        if (StringUtils.isNotBlank(requestDto.getLga())) {
            userDetails.setLga(requestDto.getLga());
        }
        if (StringUtils.isNotBlank(requestDto.getAltPhoneNumber())) {
            userDetails.setAltPhoneNumber(requestDto.getAltPhoneNumber());
        }
        if (StringUtils.isNotBlank(requestDto.getDob())) {
            userDetails.setDob(requestDto.getDob());
        }
        if (StringUtils.isNotBlank(requestDto.getProfilePhoto())) {
            userDetails.setProfilePhoto(requestDto.getProfilePhoto());
        }

        userRepository.save(userDetails);

        return BaseResponse.success("UPDATED", BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    @Transactional
    public BaseResponse createBusinessProfile(BusinessRequestDto requestDto) throws GenericErrorCodeException {
        Organization organization = organizationRepository.findByCreatorId(requestDto.getUserId())
            .orElseThrow(() -> new  GenericErrorCodeException("user not found", ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND));

        //Todo further clarification needed
//        if (StringUtils.isNotBlank(requestDto.getEmail()) && organizationRepository.isEmailTaken(requestDto.getEmail())) {
//            throw new GenericErrorCodeException("Email is already in use.", ErrorCode.BAD_REQUEST, HttpStatus.CONFLICT);
//        }
//        if (StringUtils.isNotBlank(requestDto.getPhoneNumber()) && organizationRepository.isPhoneNumberTaken(requestDto.getPhoneNumber())) {
//            throw new GenericErrorCodeException("Phone Number is already in use.", ErrorCode.BAD_REQUEST, HttpStatus.CONFLICT);
//        }

        Business business = new Business();
        business.setId(UUIDGenerator.generate());
        business.setOrganization(organization);
        business.setName(requestDto.getName());
        business.setEmail(requestDto.getEmail());
        business.setAddress(requestDto.getAddress());
        business.setCountryCode(requestDto.getCountryCode());
        business.setPhoneNumber(requestDto.getPhoneNumber());
        business.setLogoImage(requestDto.getLogoImageBase64());
        business.setBusinessType(requestDto.getBusinessType());
        businessRepository.save(business);

        cashPointService.createCashPoint(business);

        organization.addBusiness(business);
        organizationRepository.save(organization);
        return BaseResponse.success(organization, BaseResponseMessage.SUCCESSFUL);
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
        if (StringUtils.isNotBlank(requestDto.getLogoImage())) {
            businessDetails.setLogoImage(requestDto.getLogoImage());
        }
        if (StringUtils.isNotBlank(requestDto.getCountryCode())) {
            businessDetails.setCountryCode(requestDto.getCountryCode());
        }
        if (StringUtils.isNotBlank(requestDto.getPhoneNumber())) {
            businessDetails.setPhoneNumber(requestDto.getPhoneNumber());
        }

        businessRepository.save(businessDetails);

        return BaseResponse.success("UPDATED", BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    public BaseResponse getBusiness(String userId) throws GenericErrorCodeException {
        Organization organization = organizationRepository.findByCreatorId(userId)
            .orElseThrow(() -> new  GenericErrorCodeException("user not found", ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND));
        return BaseResponse.success(organization, BaseResponseMessage.SUCCESSFUL);
    }
}
