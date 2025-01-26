package com.suntrustbank.user.entrypoints.adminuser.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionDto {
    private String reference;
    private String name;
    private String category;
}
