package com.suntrustbank.user.services;

import com.suntrustbank.user.core.configs.properties.ServiceConfig;
import com.suntrustbank.user.core.configs.webclient.AbstractWebClientService;
import com.suntrustbank.user.core.configs.webclient.ProviderConfigure;
import com.suntrustbank.user.core.errorhandling.exceptions.TransactionWebClientException;
import com.suntrustbank.user.services.dtos.TransactionUserRequestDto;
import com.suntrustbank.user.services.dtos.GenericTransactionResponseDto;
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
public class TransactionWebClientService extends AbstractWebClientService<TransactionUserRequestDto, GenericTransactionResponseDto> {

    private final ServiceConfig config;

    @Override
    public ProviderConfigure configure() {
        return ProviderConfigure.builder().baseUrl(config.getTransactionServiceUrl()).build();
    }

    @Override
    public GenericTransactionResponseDto request(TransactionUserRequestDto request) {
        return callAPI(HttpMethod.POST, "/cash-point/save").
            body(BodyInserters.fromValue(request)).
            header("Authorization", request.getAuthorization()).
            exchangeToMono(this::toResponseOrError).
            doOnError(exception -> log.error("Error occurred while creating a mini copy on the user service on transaction service", exception)).
            onErrorResume(WebClientResponseException.class, this::toError).
            block();
    }

    private Mono<GenericTransactionResponseDto> toError(WebClientResponseException exception) {
        GenericTransactionResponseDto.Error error = exception.getResponseBodyAs(GenericTransactionResponseDto.Error.class);
        throw new TransactionWebClientException(error);
    }

    private Mono<GenericTransactionResponseDto> toResponseOrError(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.createError();
        }
        return clientResponse.bodyToMono(GenericTransactionResponseDto.class);
    }
}
