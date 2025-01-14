package com.suntrustbank.user.services;

import com.suntrustbank.user.core.configs.properties.ServiceConfig;
import com.suntrustbank.user.core.configs.webclient.AbstractWebClientService;
import com.suntrustbank.user.core.configs.webclient.ProviderConfigure;
import com.suntrustbank.user.core.errorhandling.exceptions.AuthWebClientException;
import com.suntrustbank.user.services.dtos.AuthOrganizationRequestDto;
import com.suntrustbank.user.services.dtos.AuthResponseDto;
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
public class AuthOrganizationWebClientService extends AbstractWebClientService<AuthOrganizationRequestDto, AuthResponseDto> {

    private final ServiceConfig config;

    @Override
    public ProviderConfigure configure() {
        return ProviderConfigure.builder().baseUrl(config.getAuthServiceUrl()).build();
    }

    @Override
    public AuthResponseDto request(AuthOrganizationRequestDto requestDto) {
        return callAPI(HttpMethod.POST, "/admin/signup").
            body(BodyInserters.fromValue(requestDto)).
            exchangeToMono(this::toResponseOrError).
            doOnError(exception -> log.error("Error occurred while creating organizationuser user on the Auth service", exception)).
            onErrorResume(WebClientResponseException.class, this::toError).
            block();
    }

    private Mono<AuthResponseDto> toError(WebClientResponseException exception) {
        AuthResponseDto.Error error = exception.getResponseBodyAs(AuthResponseDto.Error.class);
        throw new AuthWebClientException(error);
    }

    private Mono<AuthResponseDto> toResponseOrError(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.createError();
        }
        return clientResponse.bodyToMono(AuthResponseDto.class);
    }
}
