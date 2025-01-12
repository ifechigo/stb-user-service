package com.suntrustbank.user.entrypoints.user.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;
import com.suntrustbank.user.entrypoints.user.repository.models.Organization;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessResponseDto {
    private User user;
    private List<Business> businesses;

    @Getter
    @Setter
    public static class User {
        private String reference;
        private String countryCode;
        private String phoneNumber;
        private String firstName;
        private String lastName;
        private String email;
        private String address;
        private String state;
        private String lga;
        private String altCountryCode;
        private String altPhoneNumber;
        private String dob;
        private String role;
        private String profilePhoto;
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Business {
        private String reference;
        private String name;
        private String email;
        private String address;
        private String logoImage;
        private String countryCode;
        private String phoneNumber;
        private String businessType;
        private List<CashPoint> cashPoints;
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CashPoint {
        private String reference;
        private String walletReference;
        private Status status;
        private boolean main;
    }

    public static BusinessResponseDto toDto(Organization organization) {
        BusinessResponseDto businessResponseDto = new BusinessResponseDto();

        User user = new User();
        BeanUtils.copyProperties(organization.getCreator(), user);
        businessResponseDto.setUser(user);
        businessResponseDto.getUser().setRole(organization.getCreator().getRole().name());

        List<BusinessResponseDto.Business> businessDtos = organization.getBusinesses().stream()
                .map(business -> {
                    BusinessResponseDto.Business businessDto = new BusinessResponseDto.Business();
                    businessDto.setReference(business.getReference());
                    businessDto.setName(business.getName());
                    businessDto.setEmail(business.getEmail());
                    businessDto.setAddress(business.getAddress());
                    businessDto.setLogoImage(business.getLogoImage());
                    businessDto.setCountryCode(business.getCountryCode());
                    businessDto.setPhoneNumber(business.getPhoneNumber());
                    businessDto.setBusinessType(business.getBusinessType().toString());

                    List<BusinessResponseDto.CashPoint> cashPointDtos = business.getCashPoints().stream()
                            .map(cashPoint -> {
                                BusinessResponseDto.CashPoint cashPointDto = new BusinessResponseDto.CashPoint();
                                cashPointDto.setReference(cashPoint.getReference());
                                cashPointDto.setWalletReference(cashPoint.getWalletReference());
                                cashPointDto.setStatus(cashPoint.getStatus());
                                cashPointDto.setMain(cashPoint.isMain());
                                return cashPointDto;
                            }).toList();

                    businessDto.setCashPoints(cashPointDtos);
                    return businessDto;
                }).toList();

        businessResponseDto.setBusinesses(businessDtos);

        return businessResponseDto;
    }
}
