package com.centent.channel.wechat.official;

import com.centent.channel.IChannel;
import com.centent.channel.wechat.official.config.WechatOfficialConfig;
import com.centent.channel.wechat.official.retrofit.WechatOfficialAPI;
import com.centent.core.enums.Channel;
import com.centent.core.exception.HttpRequestException;
import com.centent.core.util.JSONUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import retrofit2.Response;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class WechatOfficialChannel implements IChannel {

    // 微信公众号的access_token过期时间是7200秒
    private static final Cache<String, String> ACCESS_TOKEN_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(7000, TimeUnit.SECONDS)
            .maximumSize(1)
            .build();
    @Resource
    private WechatOfficialConfig config;
    @Resource
    private WechatOfficialAPI api;

    @Override
    public Channel channel() {
        return Channel.WECHAT_OFFICE;
    }

    public String getAccessToken() {
        try {
            return ACCESS_TOKEN_CACHE.get("access_token", () -> {
                Response<Map<String, String>> response = api.getToken(
                        WechatOfficialAPI.GRANT_TYPE,
                        config.getAppId(), config.getAppSecret()
                ).execute();

                Map<String, String> body = response.body();
                if (CollectionUtils.isEmpty(body) || !body.containsKey("access_token")) {
                    throw new HttpRequestException("调用微信公众号接口错误，response body is empty --> " + JSONUtil.toJSONString(body));
                }
                return body.get("access_token");
            });
        } catch (ExecutionException e) {
            throw new HttpRequestException("调用微信公众号接口错误，获取access_token失败", e);
        }
    }
}
