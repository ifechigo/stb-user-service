package com.suntrustbank.user.core.configs.logging;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.CorrelationId;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Sink;

import java.util.Objects;

import static org.zalando.logbook.core.HeaderFilters.authorization;
import static org.zalando.logbook.core.QueryFilters.accessToken;

@Configuration
public class LogbookConfiguration {
    private static final String TRACEPARENT = "traceparent";
    private static ApplicationContext applicationContext;

    public LogbookConfiguration(ApplicationContext applicationContext) {
        LogbookConfiguration.applicationContext = applicationContext;
    }

    @Bean
    public static CorrelationId correlationId() {

        return request -> {
            SpanContext spanContext = Span.current().getSpanContext();
            String spanId = spanContext.getSpanId();
            String traceId = spanContext.getTraceId();
            String correlation = traceId + "-" + spanId;

            final String requestId = request.getHeaders().getFirst(TRACEPARENT);
            return Objects.toString(requestId, correlation);
        };
    }

    public static Logbook instance() {
        Sink sink = applicationContext.getBean(Sink.class);

        return Logbook.builder()
            .queryFilter(accessToken())
            .headerFilter(authorization())
            .correlationId(correlationId())
            .sink(sink)
            .build();
    }

    @Bean
    public Logbook logbook() {

        return instance();
    }
}
