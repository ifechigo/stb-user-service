package com.suntrustbank.user.services.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthOrganizationRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}