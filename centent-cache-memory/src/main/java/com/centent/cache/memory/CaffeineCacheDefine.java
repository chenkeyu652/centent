package com.centent.cache.memory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;

import java.util.concurrent.TimeUnit;

public class CaffeineCacheDefine {

    static final Cache<String, CaffeineWrapper<Object>> CACHE = Caffeine.newBuilder()
            .initialCapacity(1)
            .expireAfter(new Expiry<String, CaffeineWrapper<Object>>() {
                @Override
                public long expireAfterCreate(String key, CaffeineWrapper<Object> wrapper, long currentTime) {
                    return wrapper.unit.toNanos(wrapper.duration());
                }

                @Override
                public long expireAfterUpdate(String key, CaffeineWrapper<Object> emp, long currentTime, long currentDuration) {
                    return currentDuration;
                }

                @Override
                public long expireAfterRead(String key, CaffeineWrapper<Object> emp, long currentTime, long currentDuration) {
                    return currentDuration;
                }
            })
            .build();

    public record CaffeineWrapper<V>(V value, long duration, TimeUnit unit) {
    }
}
