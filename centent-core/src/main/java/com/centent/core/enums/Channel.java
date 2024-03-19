package com.centent.core.enums;

import com.centent.core.define.IBaseEnum;

public enum Channel implements IBaseEnum {

    WECHAT_OFFICE(1, "微信公众号");

    private final Integer value;

    private final String name;

    Channel(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
