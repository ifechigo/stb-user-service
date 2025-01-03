package com.suntrustbank.user.services.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericTransactionResponseDto {
    private String status;
    private String message;

    @Getter
    @Setter
    public static class Error {
        private String status;
        private List<String> messages;
        private List<String> errors;
    }
}
