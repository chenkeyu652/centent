package com.centent.channel.wechat.official.enums;

import com.centent.core.define.IBaseEnum;
import lombok.Getter;

@Getter
public enum UserStatus implements IBaseEnum {

    FORBIDDEN_SUBSCRIBE(-2, "已关注（已封禁）"),

    FORBIDDEN_UNSUBSCRIBE(-1, "未关注（已封禁）"),

    UNSUBSCRIBE(1, "未关注"),

    SUBSCRIBE(1, "已关注");

    private final Integer value;

    private final String name;

    UserStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * 翻转用户状态
     *
     * @return 翻转后的用户状态
     * @since 0.0.1
     */
    public UserStatus reverse() {
        return switch (this) {
            case FORBIDDEN_SUBSCRIBE -> FORBIDDEN_UNSUBSCRIBE;
            case FORBIDDEN_UNSUBSCRIBE -> FORBIDDEN_SUBSCRIBE;
            case UNSUBSCRIBE -> SUBSCRIBE;
            case SUBSCRIBE -> UNSUBSCRIBE;
        };
    }
}
