package com.centent.core.define;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.util.Objects;

/**
 * 枚举定义接口
 *
 * @since 0.0.1
 */
public interface IBaseEnum extends IEnum<Integer> {

    /**
     * @return 枚举显示名称
     * @since 0.0.1
     */
    String getName();

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
}
