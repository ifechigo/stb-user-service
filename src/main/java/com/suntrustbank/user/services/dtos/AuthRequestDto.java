package com.suntrustbank.user.services.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthRequestDto {
    private String userId;
    private String phoneNumber;
    private String pin;
}