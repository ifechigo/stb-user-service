package com.suntrustbank.user.entrypoints.dtos;


import com.suntrustbank.user.entrypoints.dtos.enums.BusinessType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import static com.suntrustbank.user.entrypoints.dtos.Constants.PHONE_NUMBER_LENGTH;

@Getter
@Setter
public class BusinessUpdateRequest {
    private String organizationId;
    private String fullName;

    @Email(message = "not a valid email type")
    private String email;

    private String businessAddress;
    private String state;
    private String lga;

    @Pattern(regexp = "\\d{11}", message = "Phone Number must be 11 digits")
    private String alternativePhoneNumber;

    private String photoBase64;
    private String dob;
    private BusinessType businessType;
}
