package com.suntrustbank.user.entrypoints.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessUpdateRequestDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userReference;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String businessReference;

    @Email(message = "invalid email format")
    private String email;

    private String cacNumber;
    private String logoImageBase64;
    private String countryCode;
    private String phoneNumber;

    @AssertTrue(message = "both countryCode and phoneNumber must either be provided together or left blank")
    public boolean isCountryCodeAndAltPhoneNumberValid() {
        boolean isCountryCodeBlank = StringUtils.isBlank(countryCode);
        boolean isAltPhoneNumberBlank = StringUtils.isBlank(phoneNumber);
        return (isCountryCodeBlank && isAltPhoneNumberBlank) || (!isCountryCodeBlank && !isAltPhoneNumberBlank);
    }
}
