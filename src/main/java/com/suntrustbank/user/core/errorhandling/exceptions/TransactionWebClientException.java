package com.suntrustbank.user.core.errorhandling.exceptions;


import com.suntrustbank.user.entrypoints.dtos.TransactionUserResponseDto;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Generated
public class TransactionWebClientException extends RuntimeException {
    public static final int SERVER_ERROR = 500;
    private TransactionUserResponseDto.Error error;

    public TransactionWebClientException(TransactionUserResponseDto.Error error) {
        super(error.getMessages().getFirst());
        this.error = error;
    }
}
