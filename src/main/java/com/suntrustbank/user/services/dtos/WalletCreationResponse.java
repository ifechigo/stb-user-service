package com.suntrustbank.user.services.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
public class WalletCreationResponse {
    private String status;
    private Data data;
    private String message;

    @Getter
    @Setter
    public static class Error {
        private String status;
        private List<String> messages;
        private List<String> errors;
    }

    @Getter
    @Setter
    public static class Data {
        private String walletReference;
        private String currency;
        private LocalDateTime createdAt;
    }
}
