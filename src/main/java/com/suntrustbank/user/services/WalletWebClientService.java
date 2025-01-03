package com.suntrustbank.user.services;

import com.suntrustbank.user.core.configs.properties.ServiceConfig;
import com.suntrustbank.user.core.configs.webclient.AbstractWebClientService;
import com.suntrustbank.user.core.configs.webclient.ProviderConfigure;
import com.suntrustbank.user.core.errorhandling.exceptions.WalletWebClientException;
import com.suntrustbank.user.services.dtos.WalletCreationRequest;
import com.suntrustbank.user.services.dtos.WalletCreationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletWebClientService extends AbstractWebClientService<WalletCreationRequest, WalletCreationResponse> {

    private final ServiceConfig config;

    @Override
    public ProviderConfigure configure() {
        return ProviderConfigure.builder().baseUrl(config.getWalletServiceUrl()).build();
    }

    @Override
    public WalletCreationResponse request(WalletCreationRequest requestDto) {
        return callAPI(HttpMethod.POST, "").
            body(BodyInserters.fromValue(requestDto)).
            exchangeToMono(this::toResponseOrError).
            doOnError(exception -> log.error("Error occurred while creating user on the Auth service", exception)).
            onErrorResume(WebClientResponseException.class, this::toError).
            block();
    }

    private Mono<WalletCreationResponse> toError(WebClientResponseException exception) {
        WalletCreationResponse.Error error = exception.getResponseBodyAs(WalletCreationResponse.Error.class);
        throw new WalletWebClientException(error);
    }

    private Mono<WalletCreationResponse> toResponseOrError(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.createError();
        }
        return clientResponse.bodyToMono(WalletCreationResponse.class);
    }
}
