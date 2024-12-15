package com.suntrustbank.user.core.configs.logging;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class InstrumentedWebClientConfiguration {
    private static ApplicationContext applicationContext;

    public InstrumentedWebClientConfiguration(ApplicationContext context) {
        InstrumentedWebClientConfiguration.applicationContext = context;
    }

    public static WebClient.Builder webClientBuilder() {
        return applicationContext.getBean(WebClient.Builder.class).defaultHeaders(HttpHeaders::clear);
    }

}
