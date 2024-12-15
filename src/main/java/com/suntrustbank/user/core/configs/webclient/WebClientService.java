package com.suntrustbank.user.core.configs.webclient;

public interface WebClientService<I, O> {

    ProviderConfigure configure();

    O request(I requestDto);
}
