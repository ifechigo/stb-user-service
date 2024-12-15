package com.suntrustbank.user.entrypoints.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDto {
    @NotBlank(message = "firstName field cannot be empty")
    private String firstName;

    @NotBlank(message = "lastName field cannot be empty")
    private String lastName;

    @Email(message = "invalid email format")
    private String email;

    private String address;
    private String dob;
    private String country;
}
