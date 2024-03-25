package com.centent.cache.memory;

import com.centent.cache.ICache;
import com.centent.cache.memory.CaffeineCacheDefine.CaffeineWrapper;
import com.centent.core.exception.CacheException;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MemoryCache extends ICache {

    private static final ConcurrentHashMap<String, LockData> NX_STORE = new ConcurrentHashMap<>();

    private static final Interner<String> INTERNER = Interners.newWeakInterner();


    @Override
    public <V> void save(String key, V value, Integer timeout, TimeUnit timeUnit) {
        int timeout1 = Objects.isNull(timeout) ? 0 : timeout;
        CaffeineCacheDefine.CACHE.put(this.getKey(key), new CaffeineCacheDefine.CaffeineWrapper(value, timeout1, timeUnit));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V get(String key) {
        CaffeineWrapper present = CaffeineCacheDefine.CACHE.getIfPresent(this.getKey(key));
        if (present == null) {
            return null;
        }
        return (V) present.value();
    }

    @SuppressWarnings("unchecked")
    public <V> V get(final String key, final Integer timeout, final TimeUnit timeUnit, final Callable<V> loader) {
        int timeout1 = Objects.isNull(timeout) ? 0 : timeout;
        return (V) CaffeineCacheDefine.CACHE.get(this.getKey(key), (k) -> {
            try {
                return new CaffeineWrapper(loader.call(), timeout1, timeUnit);
            } catch (Exception e) {
                throw new CacheException("缓存写入执行失败：" + this.getKey(key), e);
            }
        }).value();
    }

    @Override
    public boolean delete(String key) {
        CaffeineCacheDefine.CACHE.invalidate(this.getKey(key));
        return true;
    }

    @Override
    public long delete(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return 0;
        }
        Set<String> keySets = keys.stream().map(this::getKey).collect(Collectors.toSet());
        CaffeineCacheDefine.CACHE.invalidateAll(keySets);
        return keys.size();
    }

    @Override
    public long getExpireTime(String key) {
        CaffeineWrapper present = CaffeineCacheDefine.CACHE.getIfPresent(this.getKey(key));
        if (present != null) {
            // TODO...添加获取过期时间逻辑
        }
        return 0;
    }

    @Override
    public boolean hasKey(String key) {
        return CaffeineCacheDefine.CACHE.getIfPresent(this.getKey(key)) != null;
    }

    @Override
    public <V> Boolean lock(String key, V value, long expire, long timeout) {
        // 当前锁存在的问题：即使锁已经过期（isExpired()==true），仍然被NX_STORE持有未释放，占用内存
        long startTime = System.currentTimeMillis();
        key = this.getKey(key);
        synchronized (INTERNER.intern(key)) { // 使用guava的Interner类而不是string.intern()
            while (true) {
                // 几轮操作下来或者一直被阻塞，未获得锁，则获取锁超时
                if (timeout > 0 && System.currentTimeMillis() - startTime >= timeout) {
                    return false; // 获取锁超时
                }

                LockData newLock = new LockData(value, System.currentTimeMillis() + expire);
                LockData existsLock = NX_STORE.putIfAbsent(key, newLock);

                // 旧锁不存在或者已过期，直接插入新锁，直接获得锁
                if (existsLock == null || existsLock.isExpired()) {
                    NX_STORE.put(key, newLock);
                    return true;
                }
                // 旧锁存在，且值相同，直接获得锁
                if (Objects.equals(existsLock.value, value)) {
                    return true;
                }

                // 没有超时，等待一段时间后，继续尝试获取锁
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public <V> boolean unlock(String key, V value) {
        key = this.getKey(key);
        LockData lockData = NX_STORE.get(key);
        if (lockData == null) {
            return true;
        }
        if (Objects.equals(lockData.value, value) && NX_STORE.remove(key, lockData)) {
            return true;
        } else {
            log.error("释放锁不成功：key={}, value={}", key, value);
        }
        return false;
    }

    // 内部类，用于存储锁信息
    private record LockData(Object value, long expireTime) {
        public boolean isExpired() {
            return System.currentTimeMillis() >= expireTime;
        }
    }
}
