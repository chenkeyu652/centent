package com.centent.cache.memory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;

import java.util.concurrent.TimeUnit;

public class CaffeineCacheDefine {

    static final Cache<String, CaffeineWrapper> CACHE = Caffeine.newBuilder()
            .initialCapacity(6)
            .expireAfter(new Expiry<String, CaffeineWrapper>() {
                @Override
                public long expireAfterCreate(String key, CaffeineWrapper wrapper, long currentTime) {
                    if (wrapper.duration <= 0) {
                        return Long.MAX_VALUE;
                    }
                    return wrapper.unit.toNanos(wrapper.duration());
                }

                @Override
                public long expireAfterUpdate(String key, CaffeineWrapper emp, long currentTime, long currentDuration) {
                    // 返回更新后的过期时间，这里不做更改
                    return currentDuration;
                }

                @Override
                public long expireAfterRead(String key, CaffeineWrapper emp, long currentTime, long currentDuration) {
                    // 返回读取后的过期时间，这里不做更改
                    return currentDuration;
                }
            })
            .build();

    public record CaffeineWrapper(Object value, long duration, TimeUnit unit) {
    }
}
