package com.suntrustbank.user.entrypoints.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.suntrustbank.user.entrypoints.repository.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    private String altPhoneNumber;
    private String dob;
    private String profilePhoto;
}
