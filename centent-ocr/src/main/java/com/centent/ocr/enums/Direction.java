package com.centent.ocr.enums;

import com.centent.core.define.IBaseEnum;

public enum Direction implements IBaseEnum {
    FRONT(1, "正面"),
    BACK(2, "背面");

    private final Integer value;

    private final String name;

    Direction(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }
}
