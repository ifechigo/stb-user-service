package com.suntrustbank.user.core.configs.webclient;

import io.netty.channel.ChannelHandler;
import io.netty.handler.logging.LogLevel;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.spring.webflux.LogbookExchangeFilterFunction;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.util.*;

import static com.suntrustbank.user.core.configs.logging.InstrumentedWebClientConfiguration.webClientBuilder;

public abstract class AbstractWebClientService<I, O> implements WebClientService<I, O> {

    private WebClient webClient;

    private WebClient webClient() {
        final ProviderConfigure providerConfigure = configure();

        if (webClient != null) {
            return webClient;
        }

        if (providerConfigure == null) {
            throw new IllegalStateException("ProviderConfigure for service must not be null");
        }

        providerConfigure.validate();

        final Builder webClientBuilder = providerConfigure.isShouldUseBaseUrl()
            ? webClientBuilder().baseUrl(providerConfigure.getBaseUrl())
            : webClientBuilder();

        // set default headers
        final Map<String, String> headers = Optional
            .ofNullable(providerConfigure.getHeaders())
            .orElse(new HashMap<>());
        headers.forEach(webClientBuilder::defaultHeader);

        // set custom filter functions
        final List<ExchangeFilterFunction> exchangeFilterFunctions = Optional
            .ofNullable(providerConfigure.getFilterFunctions())
            .orElse(new ArrayList<>());

        // add filters
        exchangeFilterFunctions.forEach(webClientBuilder::filter);

        // set channel handlers
        final List<ChannelHandler> channelHandlers = Optional
            .ofNullable(providerConfigure.getChannelHandlers())
            .orElse(new ArrayList<>());
        prepareLogging(channelHandlers, webClientBuilder);

        webClient = webClientBuilder.build();

        return webClient;
    }

    private void prepareLogging(final List<ChannelHandler> channelHandlers, final Builder webClientBuilder) {
        final HttpClient httpClient = HttpClient.create().wiretap(
            "reactor.netty.http.client.HttpClient",
            LogLevel.TRACE,
            AdvancedByteBufFormat.SIMPLE
        ).doOnConnected(connection -> addHandler(channelHandlers, connection));

        webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    private void addHandler(final List<ChannelHandler> channelHandlers, final Connection connection) {
        channelHandlers.forEach(channelHandler -> connection.removeHandler(channelHandler.getClass().getName())
            .addHandlerLast(channelHandler.getClass().getName(), channelHandler));
    }

    protected WebClient.RequestBodySpec callAPI(final HttpMethod method, final String path) {
        return webClient().method(method).uri(path);
    }

    protected Mono<O> callAPI(final HttpMethod method, final String path, final Class<O> response) {
        return callAPI(method, path).exchangeToMono(clientResponse -> clientResponse.bodyToMono(response));
    }

    protected Mono<O> callAPI(final HttpMethod method, final String path, final I request, final Class<O> response) {
        return callAPI(method, path).body(BodyInserters.fromValue(request))
            .exchangeToMono(clientResponse -> clientResponse.bodyToMono(response));
    }
}
