package com.centent.core.define;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Objects;

/**
 * 枚举定义接口
 *
 * @since 0.0.1
 */
public interface IBaseEnum extends IEnum<Integer> {

    static <T extends IBaseEnum> T fromValue(Class<T> enumType, Integer value) {
        if (Objects.isNull(value)) {
            return null;
        }
        T[] enums = enumType.getEnumConstants();
        for (T item : enums) {
            if (Objects.equals(item.getValue(), value)) {
                return item;
            }
        }
        return null;
    }

    /**
     * @return 枚举显示名称
     * @since 0.0.1
     */
    String getName();

    /**
     * jackson序列化IBaseEnum，额外添加翻译字段
     *
     * @since 0.1
     */
    class IBaseEnumSerializer extends JsonSerializer<IBaseEnum> {

        private static final String TRANSLATE_LAST = "_trans";

        @Override
        public void serialize(IBaseEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value != null) {
                String fieldName = gen.getOutputContext().getCurrentName();
                gen.writeNumber(value.getValue());
                gen.writeStringField(this.getTransName(fieldName), value.getName());
            }
        }

        private String getTransName(String fieldName) {
            return fieldName + TRANSLATE_LAST;
        }
    }
}
