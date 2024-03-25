package com.centent.cache;

import com.centent.core.exception.BusinessException;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract class ICache {

    /**
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public abstract <V> void save(final String key, final V value, final Integer timeout, final TimeUnit timeUnit);

    public final <V> void save(final String key, final V value) {
        this.save(key, value, null, null);
    }

    public final <V> void save(final String key, final V value, final Integer timeout) {
        this.save(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获得缓存对象
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public abstract <V> V get(final String key);

    /**
     * 获得缓存对象，获取不到时执行loader写入缓存
     *
     * @param key      缓存键值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     * @param loader   设置缓存的实现
     * @return 缓存键值对应的数据
     */
    @SuppressWarnings("unchecked")
    public <V> V get(final String key, final Integer timeout, final TimeUnit timeUnit, final Callable<V> loader) {
        if (this.hasKey(key)) {
            return (V) this.get(key);
        }
        V value;
        try {
            value = loader.call();
        } catch (Exception e) {
            throw new BusinessException("缓存写入执行失败：" + key, e);
        }
        this.save(key, value, timeout, timeUnit);
        return value;
    }

    public final <V> V get(final String key, final Integer timeout, final Callable<V> loader) {
        return this.get(key, timeout, TimeUnit.SECONDS, loader);
    }

    public final <V> V get(final String key, final Callable<V> loader) {
        return this.get(key, null, null, loader);
    }

    /**
     * 删除单个对象
     *
     * @param key 缓存键值
     * @return 是否删除成功
     */
    public abstract boolean delete(final String key);

    /**
     * 删除集合对象
     *
     * @param keys 多个键值
     * @return 成功删除的对象数
     */
    public abstract long delete(final Collection<String> keys);

    /**
     * 根据key获取过期时间
     *
     * @param key 缓存键值
     * @return 时间(秒) 返回0代表为永久有效
     */
    public abstract long getExpireTime(String key);

    /**
     * 判断key是否存在
     *
     * @param key 缓存键值
     * @return boolean
     */
    public abstract boolean hasKey(String key);

    /**
     * 设置分布式锁，有等待时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expire  锁的时间(秒)
     * @param timeout 在timeout时间内仍未获取到锁，则获取失败
     * @return 设置成功为 true
     */
    public abstract <V> Boolean lock(String key, V value, long expire, long timeout);

    public final <V> Boolean lock(String key, V value, long expire) {
        return this.lock(key, value, expire, 0);
    }

    /**
     * 释放分布式锁
     *
     * @param key   缓存键
     * @param value 缓存值
     * @return boolean
     */
    public abstract <V> boolean unlock(String key, V value);

    public String getKey(String key) {
        return key;
    }
}
