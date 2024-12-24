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

    @NotBlank(message = "pin cannot be empty")
    private String pin;
}
