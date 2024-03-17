package com.centent.channel.wechat.official.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "centent.channel.wechat.official")
public class WechatOfficialConfig {

    /**
     * 微信公众号接口请求地址
     *
     * @since 0.0.1
     */
    private String url;

    /**
     * 微信公众号id
     *
     * @since 0.0.1
     */
    private String appId;

    /**
     * 微信公众号secret
     *
     * @since 0.0.1
     */
    private String appSecret;
}
