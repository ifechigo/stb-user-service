package com.suntrustbank.user.entrypoints.organizationuser.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationUserDto {
    private String reference;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean isTeamLead;
    private String countryCode;
    private String phoneNumber;
    private List<PermissionDto> permissions;
}
