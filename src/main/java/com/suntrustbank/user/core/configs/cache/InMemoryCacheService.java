package com.suntrustbank.user.core.configs.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class InMemoryCacheService implements CacheService {

    private static final Logger log = LoggerFactory.getLogger(InMemoryCacheService.class);
    private final Cache<String, String> cache;

    @Override
    public Boolean acquireLock(String key, String value, long ttlInMinutes) {
        if (cache.getIfPresent(key) != null) {
            return false;
        }

        Cache<String, String> temporaryCache = CacheBuilder.newBuilder()
                .expireAfterWrite(ttlInMinutes, TimeUnit.MINUTES)
                .build();

        temporaryCache.put(key, value);
        cache.putAll(temporaryCache.asMap());

        return true;
    }

    @Override
    public String get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void releaseLock(String key) {
        cache.invalidate(key);
    }
}

