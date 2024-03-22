package com.centent.cache.memory;

import com.centent.cache.ICache;
import com.centent.cache.memory.CaffeineCacheDefine.CaffeineWrapper;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemoryCache implements ICache {

    private static final ConcurrentHashMap<String, Object> NX_STORE = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public <V> void save(String key, V value, Integer timeout, TimeUnit timeUnit) {
        CaffeineCacheDefine.CACHE.put(key, new CaffeineCacheDefine.CaffeineWrapper<>(value, timeout, timeUnit));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V get(String key) {
        return (V) CaffeineCacheDefine.CACHE.getIfPresent(key);
    }

    @Override
    public boolean delete(String key) {
        CaffeineCacheDefine.CACHE.invalidate(key);
        return true;
    }

    @Override
    public long delete(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return 0;
        }
        CaffeineCacheDefine.CACHE.invalidateAll(keys);
        return keys.size();
    }

    @Override
    public long getExpireTime(String key) {
        CaffeineWrapper<Object> present = CaffeineCacheDefine.CACHE.getIfPresent(key);
        if (present == null) {
            return 0;
        }
        // TODO...
        return 0;
    }

    @Override
    public boolean hasKey(String key) {
        return CaffeineCacheDefine.CACHE.getIfPresent(key) != null;
    }

    @Override
    public synchronized <V> Boolean setNx(String key, V value, long expire) {
        if (NX_STORE.containsKey(key)) {
            return false; // 如果key已存在，则不设置
        }
        NX_STORE.put(key, value);
        return true; // 如果key不存在，则设置成功
    }

    @Override
    public synchronized <V> Boolean setNx(String key, V value, long expire, long timeout) {
        return null;
    }

    @Override
    public synchronized <V> boolean releaseNx(String key, V value) {
        return false;
    }
}
