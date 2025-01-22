package com.suntrustbank.user.entrypoints.organizationuser.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.suntrustbank.user.entrypoints.organizationuser.repository.models.OrganizationUser;
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
    private String status;
    private String profilePictureBase64;
    private String countryCode;
    private String phoneNumber;
    private List<PermissionDto> permissions;

    public static OrganizationUserDto toDto(OrganizationUser organizationUser) {
        return OrganizationUserDto.builder()
            .reference(organizationUser.getReference()).email(organizationUser.getEmail())
            .firstName(organizationUser.getFirstName()).lastName(organizationUser.getLastName())
            .role(organizationUser.getRole().name()).isTeamLead(organizationUser.isTeamLead())
            .status(organizationUser.getStatus().name())
            .countryCode(organizationUser.getCountryCode()).phoneNumber(organizationUser.getPhoneNumber())
            .profilePictureBase64(organizationUser.getProfilePhoto()).build();
    }
}
