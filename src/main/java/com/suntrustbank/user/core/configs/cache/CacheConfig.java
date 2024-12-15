package com.suntrustbank.user.core.configs.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    private static final int EXPIRY_DURATION_IN_MINUTES = 5;

    @Bean
    public Cache<String, String> inMemoryCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRY_DURATION_IN_MINUTES, TimeUnit.MINUTES)
                .build();
    }
}
