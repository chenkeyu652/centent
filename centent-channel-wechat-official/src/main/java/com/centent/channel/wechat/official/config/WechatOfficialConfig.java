package com.centent.channel.wechat.official.config;

import com.centent.core.exception.IllegalArgumentException;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

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
     * 授权给微信的服务域名
     *
     * @since 0.0.1
     */
    private String authDomain;

    /**
     * 接口配置Token
     *
     * @since 0.0.1
     */
    private String apiToken;

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

    /**
     * 微信公众号消息模板
     * key是模板标识
     *
     * @since 0.0.1
     */
    private Map<String, Template> templates = new HashMap<>();

    public String getTemplateId(String key) {
        if (Strings.isBlank(key) || !templates.containsKey(key)) {
            throw new IllegalArgumentException("没有找到对应的微信公众号模板：" + key);
        }
        return templates.get(key).getId();
    }

    public Map<String, String> getTemplateParams(String key) {
        this.getTemplateId(key);
        return templates.get(key).getParams();
    }

    @Data
    private static class Template {

        private String id;

        private Map<String, String> params;
    }
}
