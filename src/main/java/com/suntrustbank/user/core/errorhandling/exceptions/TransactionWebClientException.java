package com.suntrustbank.user.core.errorhandling.exceptions;


import com.suntrustbank.user.services.dtos.GenericTransactionResponseDto;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Generated
public class TransactionWebClientException extends RuntimeException {
    public static final int SERVER_ERROR = 500;
    private GenericTransactionResponseDto.Error error;

    public TransactionWebClientException(GenericTransactionResponseDto.Error error) {
        super(error.getMessages().getFirst());
        this.error = error;
    }
}
