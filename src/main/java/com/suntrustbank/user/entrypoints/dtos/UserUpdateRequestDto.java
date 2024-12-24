package com.suntrustbank.user.entrypoints.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userId;

    private String firstName;
    private String lastName;

    @Email(message = "invalid email format")
    private String email;

    private String address;
    private String state;
    private String lga;
    private String altCountryCode;
    private String altPhoneNumber;
    private String dob;
    private String profilePhotoBase64;

    @AssertTrue(message = "both altCountryCode and altPhoneNumber must either be provided together or left blank")
    public boolean isCountryCodeAndAltPhoneNumberValid() {
        boolean isAltCountryCodeBlank = StringUtils.isBlank(altCountryCode);
        boolean isAltPhoneNumberBlank = StringUtils.isBlank(altPhoneNumber);
        return (isAltCountryCodeBlank && isAltPhoneNumberBlank) || (!isAltCountryCodeBlank && !isAltPhoneNumberBlank);
    }
}
