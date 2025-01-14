package com.suntrustbank.user.entrypoints.organizationuser.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationUserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String countryCode;
    private String phoneNumber;
}
