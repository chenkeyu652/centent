package com.centent.channel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotifyContext {

    /**
     * 消息接收人
     *
     * @since 0.0.1
     */
    private String target;

    /**
     * 消息标题
     *
     * @since 0.0.1
     */
    private String title;

    /**
     * 消息内容
     *
     * @since 0.0.1
     */
    private String content;
}
