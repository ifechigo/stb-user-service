package com.suntrustbank.user.entrypoints.adminuser.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleReassignmentRequest {
    @NotBlank(message = "reference field is required")
    private String reference;

    @NotBlank(message = "role field is required")
    private String role;
}
