package com.centent.core.enums;

import com.centent.core.define.IBaseEnum;
import lombok.Getter;

@Getter
public enum PageType implements IBaseEnum {

    NEW(1, "新建"),

    EDIT(2, "编辑"),

    VIEW(3, "查看");

    private final Integer value;

    private final String name;

    PageType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
