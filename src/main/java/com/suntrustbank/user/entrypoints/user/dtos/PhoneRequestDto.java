package com.suntrustbank.user.entrypoints.user.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneRequestDto {
    @NotBlank(message = "reference field cannot be empty")
    private String reference;

    @NotBlank(message = "otp field cannot be empty")
    @Pattern(regexp = "\\d{6}", message = "otp must be 6 digits")
    private String otp;
}
