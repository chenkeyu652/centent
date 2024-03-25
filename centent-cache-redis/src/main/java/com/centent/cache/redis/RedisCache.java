package com.centent.cache.redis;

import com.centent.cache.ICache;
import com.centent.core.util.CententUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RedisCache extends ICache {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${centent.cache.key-prefix:centent:default}")
    private String keyPrefix;

    public <V> void save(final String key, final V value, final Integer timeout, final TimeUnit timeUnit) {
        if (Objects.isNull(timeout) || timeout <= 0 || Objects.isNull(timeUnit)) {
            redisTemplate.opsForValue().set(this.getKey(key), value);
            return;
        }
        redisTemplate.opsForValue().set(this.getKey(key), value, timeout, timeUnit);
    }

    @SuppressWarnings("unchecked")
    public <V> V get(final String key) {
        BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(this.getKey(key));
        return (V) operations.get();
    }

    public boolean delete(final String key) {
        return Objects.equals(Boolean.TRUE, redisTemplate.delete(this.getKey(key)));
    }

    public long delete(final Collection<String> keys) {
        Set<String> keySets = keys.stream().map(this::getKey).collect(Collectors.toSet());
        Long deleted = redisTemplate.delete(keySets);
        return Optional.ofNullable(deleted).orElse(0L);
    }

    public long getExpireTime(String key) {
        Long expire = redisTemplate.getExpire(this.getKey(key), TimeUnit.SECONDS);
        return Optional.ofNullable(expire).orElse(0L);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(this.getKey(key)));
    }

    public <V> Boolean lock(String key, V value, long expire, long timeout) {
        long start = System.currentTimeMillis();
        // 在一定时间内获取锁，超时则返回错误
        for (; ; ) {
            // 获取到锁，并设置过期时间返回true
            Boolean result = redisTemplate.opsForValue()
                    .setIfAbsent(this.getKey(key), value, expire, TimeUnit.SECONDS);
            if (Objects.equals(Boolean.TRUE, result)) {
                return true;
            }
            // 否则循环等待，在timeout时间内仍未获取到锁，则获取失败
            if (timeout > 0 && System.currentTimeMillis() - start > timeout) {
                return false;
            }

            // 没有超时，等待一段时间后，继续尝试获取锁
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public <V> boolean unlock(String key, V value) {
        String storeKey = this.getKey(key);
        Object currentValue = redisTemplate.opsForValue().get(storeKey);
        if (Objects.equals(value, currentValue)) {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().getOperations().delete(storeKey));
        }
        return false;
    }

    public String getKey(String key) {
        if (CententUtil.uninitialized(key)) {
            throw new IllegalArgumentException("缓存的key不能为空");
        }
        return keyPrefix + ":" + key;
    }
}
