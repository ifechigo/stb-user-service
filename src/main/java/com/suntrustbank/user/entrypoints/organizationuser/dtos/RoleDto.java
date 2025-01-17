package com.suntrustbank.user.entrypoints.organizationuser.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDto {
    private String reference;
    private String name;
    private String roleType;
    private String description;
}
