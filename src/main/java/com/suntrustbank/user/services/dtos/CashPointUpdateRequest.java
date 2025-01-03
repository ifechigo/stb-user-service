package com.suntrustbank.user.services.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class CashPointUpdateRequest {
    private String cashPointReference;
    private String walletReference;
}
