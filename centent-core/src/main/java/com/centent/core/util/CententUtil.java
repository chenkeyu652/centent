package com.centent.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 通用工具类
 *
 * @since 0.0.1
 */
@Slf4j
public class CententUtil {

    public static boolean initialized(Object value) {
        if (value instanceof String s) {
            return Strings.isNotBlank(s) && !s.equals("null");
        }
        return !ObjectUtils.isEmpty(value);
    }

    public static boolean uninitialized(Object value) {
        return !initialized(value);
    }

    public static <T> T getOrDefault(Object value, T defaultValue) {
        if (initialized(value)) {
            return (T) value;
        }
        return defaultValue;

    }

    /**
     * 从给定的参数中找出第一个初始化的值，如果均未初始化，返回null
     *
     * @param values 可变参数
     * @param <T>    参数类型
     * @return 第一个初始化的值，如果均未初始化，返回null
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
    public static <T> T firstInitialized(T... values) {
        if (ObjectUtils.isEmpty(values)) {
            return null;
        }
        for (T value : values) {
            if (initialized(value)) {
                return value;
            }
        }
        return null;
    }

    public static <T> void executeIf(Predicate<T> predicate, T value, Consumer<T> consumer) {
        if (predicate.test(value)) {
            consumer.accept(value);
        }
    }
}
