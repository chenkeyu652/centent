package com.centent.channel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class NotifyContext {

    /**
     * 消息类型
     *
     * @since 0.0.1
     */
    private String type;

    /**
     * 消息接收人
     *
     * @since 0.0.1
     */
    private String target;

    /**
     * 消息数据
     *
     * @since 0.0.1
     */
    private Map<String, String> params;
}
