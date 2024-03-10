package com.centent.core.util;

import com.centent.core.configuration.JacksonMapperConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 通用工具类
 *
 * @since 0.0.1
 */
@Slf4j
public class CententUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        SimpleModule module = new SimpleModule();
        // LocalDateTime 序列化配置
        module.addSerializer(LocalDateTime.class, new JacksonMapperConfiguration.LocalDateTimestampSerializer());
        module.addDeserializer(LocalDateTime.class, new JacksonMapperConfiguration.LocalDateTimestampDeserializer());
        OBJECT_MAPPER.registerModule(module);
    }

    public static boolean initialized(Object value) {
        if (value instanceof String s) {
            return Strings.isNotBlank(s);
        }
        return !ObjectUtils.isEmpty(value);
    }

    public static boolean uninitialized(Object value) {
        return !initialized(value);
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

    public static String object2Json(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public static <T> T json2Object(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> object2Map(Object obj) {
        if (Objects.isNull(obj)) {
            return new HashMap<>();
        }
        try {
            return OBJECT_MAPPER.convertValue(obj, Map.class);
        } catch (Exception e) {
            log.error("", e);
        }
        return new HashMap<>();
    }

    public static <T> T map2Object(Map<String, Object> map, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.convertValue(map, clazz);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
