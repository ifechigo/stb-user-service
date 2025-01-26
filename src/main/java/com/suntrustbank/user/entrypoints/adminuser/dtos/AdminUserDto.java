package com.suntrustbank.user.entrypoints.adminuser.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.suntrustbank.user.entrypoints.adminuser.repository.models.AdminUser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminUserDto {
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

    public static AdminUserDto toDto(AdminUser adminUser) {
        return AdminUserDto.builder()
            .reference(adminUser.getReference()).email(adminUser.getEmail())
            .firstName(adminUser.getFirstName()).lastName(adminUser.getLastName())
            .role(adminUser.getRole().name()).isTeamLead(adminUser.isTeamLead())
            .status(adminUser.getStatus().name())
            .countryCode(adminUser.getCountryCode()).phoneNumber(adminUser.getPhoneNumber())
            .profilePictureBase64(adminUser.getProfilePhoto()).build();
    }
}
