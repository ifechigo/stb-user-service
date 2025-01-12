package com.suntrustbank.user.entrypoints.user.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

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
