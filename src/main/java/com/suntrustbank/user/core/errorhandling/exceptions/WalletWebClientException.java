package com.suntrustbank.user.core.errorhandling.exceptions;


import com.suntrustbank.user.services.dtos.WalletCreationResponse;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Generated
public class WalletWebClientException extends RuntimeException {
    public static final int SERVER_ERROR = 500;
    private WalletCreationResponse.Error error;

    public WalletWebClientException(WalletCreationResponse.Error error) {
        super(error.getMessages().getFirst());
        this.error = error;
    }
}
