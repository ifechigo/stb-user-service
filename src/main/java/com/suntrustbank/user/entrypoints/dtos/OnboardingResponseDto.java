package com.suntrustbank.user.entrypoints.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OnboardingResponseDto {
    private String reference;
}
