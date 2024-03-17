package com.centent.channel.wechat.official.bean;

import com.centent.core.exception.BusinessException;
import com.centent.core.util.CententUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.common.util.StringUtils;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 微信公众号菜单配置
 *
 * @since 0.0.1
 */
@Data
public class OfficialMenu {

    /**
     * 一级菜单数组，个数应为1~3个
     *
     * @since 0.0.1
     */
    private List<Button> button;

    private OfficialMenu() {
    }

    public static String getViewRequestUrl(String url, Map<String, Object> params) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        if (CollectionUtils.isEmpty(params)) {
            return url;
        }
        String requestUrl = url;
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (CententUtil.uninitialized(value)) {
                value = "";
            }
            requestUrl = requestUrl.replace("{" + key + "}", value.toString());
        }
        return requestUrl;
    }

    @Data
    public static class Button {

        /**
         * （必填）菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型
         *
         * @since 0.0.1
         */
        private String type;

        /**
         * （必填）菜单标题，不超过16个字节，子菜单不超过60个字节
         *
         * @since 0.0.1
         */
        private String name;

        /**
         * （click等点击类型必填）菜单KEY值，用于消息接口推送，不超过128字节
         * 本项目附加功能：（必填且必须唯一）view类型的菜单，该值将作为望网页鉴权的state参数
         *
         * @since 0.0.1
         */
        private String key;

        /**
         * （view、miniprogram类型必须）网页 链接，用户点击菜单可打开链接，不超过1024字节。 type为miniprogram时，不支持小程序的老版本客户端将打开本url。
         *
         * @since 0.0.1
         */
        private String url;

        /**
         * （media_id类型和view_limited类型必须）调用新增永久素材接口返回的合法media_id
         *
         * @since 0.0.1
         */
        private String media_id;

        /**
         * （miniprogram类型必须）小程序的appid（仅认证公众号可配置）
         *
         * @since 0.0.1
         */
        private String appid;

        /**
         * （miniprogram类型必须）小程序的页面路径
         *
         * @since 0.0.1
         */
        private String pagepath;

        /**
         * （article_id类型和article_view_limited类型必须）发布后获得的合法 article_id
         *
         * @since 0.0.1
         */
        private String article_id;

        /**
         * 二级菜单数组，个数应为1~5个
         *
         * @since 0.0.1
         */
        private List<Button> sub_button;

        @JsonIgnore
        private String originalUrl;

        public boolean isValidView() {
            if (!"view".equals(type)) {
                return false;
            }
            if (CententUtil.uninitialized(key)) {
                throw new BusinessException("view类型菜单的key参数不能为空，name=" + name);
            }
            return CententUtil.initialized(name) && CententUtil.initialized(url);
        }
    }
}
