package com.suntrustbank.user.entrypoints.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import static com.suntrustbank.user.entrypoints.dtos.Constants.PIN_LENGTH;

@Getter
@Setter
public class UserRequestDto {
    @NotBlank(message = "reference cannot be empty")
    private String reference;

    @NotBlank(message = "otp cannot be empty")
    private String otp;

    @NotBlank(message = "phone number cannot be empty")
    @Pattern(regexp = "\\d{11}", message = "phoneNumber field must be exactly 11 digits")
    private String phoneNumber;

    @NotBlank(message = "pin cannot be empty")
    @Size(min = PIN_LENGTH, message = "pin must be 4 digits long")
    @Pattern(regexp = "\\d{4}", message = "Pin must only contain number/digits")
    private String pin;
}
