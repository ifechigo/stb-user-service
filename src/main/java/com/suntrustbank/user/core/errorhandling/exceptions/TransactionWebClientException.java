package com.suntrustbank.user.core.errorhandling.exceptions;


import com.suntrustbank.user.entrypoints.dtos.AuthResponseDto;
import com.suntrustbank.user.entrypoints.dtos.UserCopyResponseDto;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Generated
public class TransactionWebClientException extends RuntimeException {
    public static final int SERVER_ERROR = 500;
    private UserCopyResponseDto.Error error;

    public TransactionWebClientException(UserCopyResponseDto.Error error) {
        super(error.getMessages().getFirst());
        this.error = error;
    }
}
