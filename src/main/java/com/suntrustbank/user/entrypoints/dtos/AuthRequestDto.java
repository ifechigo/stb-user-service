package com.suntrustbank.user.entrypoints.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthRequestDto {
    private String organizationId;
    private String phoneNumber;
    private String pin;
}