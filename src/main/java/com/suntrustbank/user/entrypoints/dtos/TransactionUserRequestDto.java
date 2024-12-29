package com.suntrustbank.user.entrypoints.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionUserRequestDto {
    @JsonIgnore
    private String authorization;

    private String creatorReference;
    private String userReference;
    private String userFullName;
    private String businessReference;
    private String businessName;
    private String cashPointReference;
    private String walletId;
}
