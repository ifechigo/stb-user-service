package com.suntrustbank.user.services.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class WalletCreationRequest {
    private String currency;
}
