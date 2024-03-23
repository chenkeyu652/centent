package com.centent.channel.wechat.official.bean;

import lombok.Data;

import java.util.Map;

@Data
public class TemplateMessage {

    /**
     * 接收者openid
     *
     * @since 0.0.1
     */
    private String touser;

    /**
     * 模板ID
     *
     * @since 0.0.1
     */
    private String template_id;

    /**
     * 模板跳转链接（海外账号没有跳转能力）
     *
     * @since 0.0.1
     */
    private String url;

    /**
     * 跳小程序所需数据，不需跳小程序可不用传该数据
     *
     * @since 0.0.1
     */
    private Map<String, String> miniprogram;

    /**
     * 所需跳转到的小程序appid（该小程序appid必须与发模板消息的公众号是绑定关联关系，暂不支持小游戏）
     *
     * @since 0.0.1
     */
    private String appid;

    /**
     * 所需跳转到小程序的具体页面路径，支持带参数,（示例index?foo=bar），要求该小程序已发布，暂不支持小游戏
     *
     * @since 0.0.1
     */
    private String pagepath;

    /**
     * 模板数据
     *
     * @since 0.0.1
     */
    private Map<String, Map<String, String>> data;

    /**
     * 防重入id。对于同一个openid + client_msg_id, 只发送一条消息,10分钟有效,超过10分钟不保证效果。若无防重入需求，可不填
     *
     * @since 0.0.1
     */
    private String client_msg_id;
}
