package com.centent.core.configuration;

import com.centent.core.define.IBaseEnum;
import com.centent.core.exception.IllegalArgumentException;
import jakarta.annotation.Nullable;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.Objects;

public class IBaseEnumConverter implements ConverterFactory<String, IBaseEnum> {

    @NotNull
    @Override
    public <T extends IBaseEnum> Converter<String, T> getConverter(@NotNull Class<T> targetType) {
        return new StringToEnum<>(targetType);
    }

    private record StringToEnum<T extends IBaseEnum>(Class<T> targetType) implements Converter<String, T> {

        @Override
        public T convert(@Nullable String source) {
            if (Strings.isBlank(source)) {
                return null;
            }
            int value;
            try {
                value = Integer.parseInt(source);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to convert value to required type, " +
                        "value: " + source + ", type: " + targetType.getName(), e);
            }

            T[] enums = targetType.getEnumConstants();
            if (null == enums) {
                return null;
            }
            for (T e : enums) {
                if (Objects.equals(e.getValue(), value)) {
                    return e;
                }
            }
            return null;
        }
    }
}
