package com.centent.core.util;

import com.centent.core.configuration.JacksonMapperConfiguration.LocalDateTimestampDeserializer;
import com.centent.core.configuration.JacksonMapperConfiguration.LocalDateTimestampSerializer;
import com.centent.core.define.IBaseEnum;
import com.centent.core.define.IBaseEnum.IBaseEnumSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class JSONUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        SimpleModule module = new SimpleModule();
        // IBaseEnum 枚举字段添加翻译字段
        module.addSerializer(IBaseEnum.class, new IBaseEnumSerializer());
        // LocalDateTime 序列化配置
        module.addSerializer(LocalDateTime.class, new LocalDateTimestampSerializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimestampDeserializer());
        OBJECT_MAPPER.registerModule(module);
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
