package com.centent.cache;

import com.centent.core.exception.BusinessException;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public interface ICache {

    /**
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    <V> void save(final String key, final V value, final Integer timeout, final TimeUnit timeUnit);

    /**
     * 缓存对象
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    default <V> void save(final String key, final V value) {
        this.save(key, value, null, null);
    }

    /**
     * 缓存对象
     *
     * @param key     缓存的键值
     * @param value   缓存的值
     * @param timeout 时间
     */
    default <V> void save(final String key, final V value, final Integer timeout) {
        this.save(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获得缓存对象
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    <V> V get(final String key);

    /**
     * 获得缓存对象
     *
     * @param key      缓存键值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     * @return 缓存键值对应的数据
     */
    @SuppressWarnings("unchecked")
    default <V> V get(final String key, final Integer timeout, final TimeUnit timeUnit, final Callable<V> loader) {
        if (this.hasKey(key)) {
            return (V) this.get(key);
        }
        V value;
        try {
            value = loader.call();
        } catch (Exception e) {
            throw new BusinessException(e);
        }
        this.save(key, value, timeout, timeUnit);
        return value;
    }

    /**
     * 获得缓存对象
     *
     * @param key     缓存键值
     * @param timeout 时间
     * @return 缓存键值对应的数据
     */
    default <V> V get(final String key, final Integer timeout, final Callable<V> loader) {
        return this.get(key, timeout, TimeUnit.SECONDS, loader);
    }

    /**
     * 获得缓存对象
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    default <V> V get(final String key, final Callable<V> loader) {
        return this.get(key, null, null, loader);
    }

    /**
     * 删除单个对象
     *
     * @param key 缓存键值
     * @return 是否删除成功
     */
    boolean delete(final String key);

    /**
     * 删除集合对象
     *
     * @param keys 多个键值
     * @return 成功删除的对象数
     */
    long delete(final Collection<String> keys);

    /**
     * 根据key获取过期时间
     *
     * @param key 缓存键值
     * @return 时间(秒) 返回0代表为永久有效
     */
    long getExpireTime(String key);

    /**
     * 判断key是否存在
     *
     * @param key 缓存键值
     * @return boolean
     */
    boolean hasKey(String key);

    /**
     * 设置分布式锁
     *
     * @param key    缓存键
     * @param value  缓存值
     * @param expire 锁的时间(秒)
     * @return 设置成功为 true
     */
    <V> Boolean setNx(String key, V value, long expire);

    /**
     * 设置分布式锁，有等待时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expire  锁的时间(秒)
     * @param timeout 在timeout时间内仍未获取到锁，则获取失败
     * @return 设置成功为 true
     */
    <V> Boolean setNx(String key, V value, long expire, long timeout);

    /**
     * 释放分布式锁
     *
     * @param key   缓存键
     * @param value 缓存值
     * @return boolean
     */
    <V> boolean releaseNx(String key, V value);
}
