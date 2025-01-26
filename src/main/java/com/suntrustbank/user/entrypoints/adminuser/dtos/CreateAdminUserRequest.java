package com.suntrustbank.user.entrypoints.adminuser.dtos;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAdminUserRequest {
    @NotBlank(message = "firstName field is required")
    private String firstName;

    @NotBlank(message = "lastName field is required")
    private String lastName;

    @NotBlank(message = "email field is required")
    private String email;

    @NotBlank(message = "roleReference field is required")
    private String roleReference;

    private String password;

    private String imageBase64;

    private String countryCode;
    private String phoneNumber;

    @AssertTrue(message = "countryCode and phoneNumber fields are both required")
    public boolean isRequired() {
        return StringUtils.isBlank(countryCode) || StringUtils.isBlank(phoneNumber);
    }
}
