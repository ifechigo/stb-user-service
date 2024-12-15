package com.suntrustbank.user.core.configs.cache;

public interface CacheService {
    Boolean acquireLock(String key, String value, long ttlInSeconds);
    String get(String key);
    void releaseLock(String key);
}
