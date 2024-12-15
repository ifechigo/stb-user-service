package com.suntrustbank.user.services;

import com.suntrustbank.user.core.configs.properties.ServiceConfig;
import com.suntrustbank.user.core.configs.webclient.AbstractWebClientService;
import com.suntrustbank.user.core.configs.webclient.ProviderConfigure;
import com.suntrustbank.user.core.errorhandling.exceptions.AuthWebClientException;
import com.suntrustbank.user.entrypoints.dtos.AuthRequestDto;
import com.suntrustbank.user.entrypoints.dtos.AuthResponseDto;
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
public class AuthWebClientService extends AbstractWebClientService<AuthRequestDto, AuthResponseDto> {

    private final ServiceConfig config;

    @Override
    public ProviderConfigure configure() {
        return ProviderConfigure.builder().baseUrl(config.getAuthServiceUrl()).build();
    }

    @Override
    public AuthResponseDto request(AuthRequestDto requestDto) {
        return callAPI(HttpMethod.POST, "/user/signup").
            body(BodyInserters.fromValue(requestDto)).
            exchangeToMono(this::toResponseOrError).
            doOnError(exception -> log.error("Error occurred while creating user on the Auth service", exception)).
            onErrorResume(WebClientResponseException.class, this::toError).
            block();

    }

    private Mono<AuthResponseDto> toError(WebClientResponseException exception) {

        System.out.println("================================================================");
        System.out.println("================================================================");

        System.out.println(exception.getMessage());
        System.out.println(exception.getResponseBodyAsString());

        System.out.println("================================================================");

        AuthResponseDto.Error error = exception.getResponseBodyAs(AuthResponseDto.Error.class);

        System.out.println(error.getMessages());
        System.out.println(error.getStatus());

        System.out.println("================================================================");
        System.out.println("================================================================");


        throw new AuthWebClientException(error);
    }

    private Mono<AuthResponseDto> toResponseOrError(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.createError();
        }
        return clientResponse.bodyToMono(AuthResponseDto.class);
    }
}
