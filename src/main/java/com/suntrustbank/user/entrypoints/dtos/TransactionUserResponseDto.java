package com.suntrustbank.user.entrypoints.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionUserResponseDto {
    private String status;
//    private String data;
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
        private long expiresIn;
        private String token;
        private String tokenType;
    }
}
