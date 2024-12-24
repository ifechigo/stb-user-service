package com.suntrustbank.user.entrypoints.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    @NotBlank(message = "country code is required and cannot be empty")
    private String countryCode;

    @NotBlank(message = "phone number is required and cannot be empty")
    private String phoneNumber;
}
