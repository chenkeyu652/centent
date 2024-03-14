package com.centent.core.enums;

import com.centent.core.define.IBaseEnum;

public enum YesNo implements IBaseEnum {

    YES(1, "是"),

    NO(0, "否");

    private final Integer value;

    private final String name;

    YesNo(Integer value, String name) {
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
