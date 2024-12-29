package com.suntrustbank.user.services;

import com.suntrustbank.user.core.configs.properties.ServiceConfig;
import com.suntrustbank.user.core.configs.webclient.AbstractWebClientService;
import com.suntrustbank.user.core.configs.webclient.ProviderConfigure;
import com.suntrustbank.user.core.errorhandling.exceptions.AuthWebClientException;
import com.suntrustbank.user.entrypoints.dtos.AuthResponseDto;
import com.suntrustbank.user.entrypoints.dtos.UserCopyRequestDto;
import com.suntrustbank.user.entrypoints.dtos.UserCopyResponseDto;
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
public class TransactionWebClientService extends AbstractWebClientService<UserCopyRequestDto, UserCopyResponseDto> {

    private final ServiceConfig config;

    @Override
    public ProviderConfigure configure() {
        return ProviderConfigure.builder().baseUrl(config.getTransactionServiceUrl()).build();
    }

    @Override
    public UserCopyResponseDto request(UserCopyRequestDto request) {
        return callAPI(HttpMethod.POST, "/copy").
            body(BodyInserters.fromValue(request)).
            header("Authorization", request.getAuthorization()).
            exchangeToMono(this::toResponseOrError).
            doOnError(exception -> log.error("Error occurred while creating a mini copy on the user service on transaction service", exception)).
            onErrorResume(WebClientResponseException.class, this::toError).
            block();
    }

    private Mono<UserCopyResponseDto> toError(WebClientResponseException exception) {
        AuthResponseDto.Error error = exception.getResponseBodyAs(AuthResponseDto.Error.class);
        throw new AuthWebClientException(error);
    }

    private Mono<UserCopyResponseDto> toResponseOrError(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.createError();
        }
        return clientResponse.bodyToMono(UserCopyResponseDto.class);
    }
}
