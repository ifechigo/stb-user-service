package com.suntrustbank.user.entrypoints.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessUpdateRequestDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userId;

    @NotBlank(message = "businessId is required and cannot be empty")
    private String businessId;

    @Email(message = "invalid email format")
    private String email;

    private String cacNumber;
    private String logoImage;
    private String countryCode;
    private String phoneNumber;
}
