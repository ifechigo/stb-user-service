package com.suntrustbank.user.entrypoints.organizationuser.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusRequest {
    @NotBlank(message = "reference field is required")
    private String reference;

    @NotBlank(message = "status field is required")
    private String status;
}
