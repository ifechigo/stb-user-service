package com.suntrustbank.user.entrypoints.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuthResponseDto {
    private String status;
    private Data data;
    private String message;

    @Getter
    @Setter
    public static class Error {
        private String status;
        private List<String> messages;
        private String message;
    }

    @Getter
    @Setter
    public static class Data {
        private long expiresIn;
        private String token;
        private String tokenType;
    }
}
