package com.suntrustbank.user.entrypoints.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCopyRequestDto {
    @JsonIgnore
    private String authorization;

    private String creatorId;
    private String userId;
    private String userFullName;
    private String businessId;
    private String businessName;
    private String cashPointId;
    private String walletId;
}
