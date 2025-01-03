package com.suntrustbank.user.entrypoints.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.suntrustbank.user.entrypoints.repository.enums.Status;
import com.suntrustbank.user.entrypoints.repository.models.Organization;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessResponseDto {
    private List<Business> businesses;

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
