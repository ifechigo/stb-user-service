package com.suntrustbank.user.entrypoints.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.suntrustbank.user.entrypoints.dtos.enums.BusinessType;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessRequestDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userId;

    @NotBlank(message = "business name is required and cannot be empty")
    private String name;

    @Email(message = "not a valid email type")
    private String email;

    @NotBlank(message = "business address is required and cannot be empty")
    private String address;

    private String countryCode;
    private String phoneNumber;
    private String logoImageBase64;
    private BusinessType businessType;

    @AssertTrue(message = "both countryCode and phoneNumber must either be provided together or left blank")
    public boolean isCountryCodeAndAltPhoneNumberValid() {
        boolean isCountryCodeBlank = StringUtils.isBlank(countryCode);
        boolean isAltPhoneNumberBlank = StringUtils.isBlank(phoneNumber);
        return (isCountryCodeBlank && isAltPhoneNumberBlank) || (!isCountryCodeBlank && !isAltPhoneNumberBlank);
    }
}
