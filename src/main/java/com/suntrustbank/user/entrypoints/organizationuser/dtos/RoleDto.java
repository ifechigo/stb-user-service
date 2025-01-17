package com.suntrustbank.user.entrypoints.organizationuser.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.suntrustbank.user.entrypoints.organizationuser.repository.enums.OrganizationRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDto {
    private String reference;
    private String name;
    private boolean isTeamLead;
    private String description;
}
