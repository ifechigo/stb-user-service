package com.suntrustbank.user.entrypoints.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.suntrustbank.user.entrypoints.dtos.enums.BusinessType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessUpdateRequest {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userId;

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
