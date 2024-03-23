package com.centent.channel.wechat.official.enums;

import com.centent.core.define.IBaseEnum;
import lombok.Getter;

@Getter
public enum UserRule implements IBaseEnum {

    SALES(1, "业务员"),

    MANAGER(2, "业务经理"),

    NORMAL(3, "普通用户");

    private final Integer value;

    private final String name;

    UserRule(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
