package com.suntrustbank.user.core.errorhandling.exceptions;


import com.suntrustbank.user.services.dtos.AuthResponseDto;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Generated
public class AuthWebClientException extends RuntimeException {
    public static final int SERVER_ERROR = 500;
    private AuthResponseDto.Error error;

    public AuthWebClientException(AuthResponseDto.Error error) {
        super(error.getMessages().getFirst());
        this.error = error;
    }
}
