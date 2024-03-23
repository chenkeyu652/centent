package com.centent.channel.wechat.official;

import com.centent.channel.IChannel;
import com.centent.channel.NotifyContext;
import com.centent.channel.wechat.official.bean.OfficialMenu;
import com.centent.channel.wechat.official.bean.SNSToken;
import com.centent.channel.wechat.official.bean.TemplateMessage;
import com.centent.channel.wechat.official.config.WechatOfficialConfig;
import com.centent.channel.wechat.official.entity.WechatOfficialUser;
import com.centent.channel.wechat.official.enums.UserStatus;
import com.centent.channel.wechat.official.retrofit.WechatOfficialAPI;
import com.centent.channel.wechat.official.service.WechatOfficialUserService;
import com.centent.core.enums.Channel;
import com.centent.core.exception.BusinessException;
import com.centent.core.exception.HttpRequestException;
import com.centent.core.exception.IllegalArgumentException;
import com.centent.core.util.JSONUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
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

    @Resource
    private WechatOfficialUserService userService;

    @Override
    public Channel channel() {
        return Channel.WECHAT_OFFICE;
    }

    @Override
    public boolean available(String owner) {
        this.config(owner);
        return true;
    }

    @Override
    public Object config(String owner) {
        WechatOfficialUser user = userService.getIfExistByOpenid(owner);
        if (Objects.isNull(user) || user.getStatus() == UserStatus.UNSUBSCRIBE) {
            throw new BusinessException("请关注本微信公众号后再使用，谢谢");
        }
        if (!user.isAvailable()) {
            log.debug("用户已被禁用但仍然尝试使用本微信公众号，openid={}", owner);
            throw new HttpRequestException("系统繁忙，请稍后再试");
        }
        return user;
    }

    @Override
    public void sendNotify(NotifyContext context) {
        String openid = context.getTarget();
        if (Objects.isNull(openid)) {
            throw new IllegalArgumentException("微信公众号发送消息失败，openid不能为空");
        }

        // TODO...需要先判断用户是否关注公众号

        Map<String, String> params = context.getParams();
        try {
            Map<String, Map<String, String>> data = new HashMap<>();
            config.getTemplateParams(context.getType())
                    .forEach(key -> data.put(key, Map.of("value", params.get(key))));

            TemplateMessage message = new TemplateMessage();
            message.setTouser(openid);
            message.setTemplate_id(config.getTemplateId(context.getType()));
            message.setUrl(params.get("url"));
            message.setData(data);
            message.setClient_msg_id(System.currentTimeMillis() + "");

            Response<WechatOfficialAPI.APIResult> response = api.sendTemplateMessage(this.getAccessToken(), message)
                    .execute();
            WechatOfficialAPI.APIResult body = response.body();
            if (Objects.isNull(body) || !Objects.equals(body.getErrcode(), "0")) {
                throw new HttpRequestException("调用微信公众号发送模板消息接口错误，response body --> " + JSONUtil.toJSONString(body));
            }
        } catch (IOException e) {
            throw new HttpRequestException("调用微信公众号发送模板消息接口错误", e);
        }

    }

    public void createMenu(OfficialMenu menu) {
        String accessToken = this.getAccessToken();
        try {
            Response<WechatOfficialAPI.APIResult> response = api.createMenu(accessToken, menu).execute();
            WechatOfficialAPI.APIResult body = response.body();
            if (Objects.isNull(body) || !Objects.equals(body.getErrcode(), "0")) {
                throw new HttpRequestException("调用微信公众号创建菜单接口错误，response body --> " + JSONUtil.toJSONString(body));
            }
        } catch (IOException e) {
            throw new HttpRequestException("调用微信公众号创建菜单接口错误", e);
        }
    }

    private String getAccessToken() {
        try {
            return ACCESS_TOKEN_CACHE.get("access_token", () -> {
                Response<Map<String, String>> response = api.getAPIToken(
                        WechatOfficialAPI.API_GRANT_TYPE,
                        config.getAppId(), config.getAppSecret()
                ).execute();

                Map<String, String> body = response.body();
                if (CollectionUtils.isEmpty(body) || !body.containsKey("access_token")) {
                    throw new HttpRequestException("调用微信公众号接口错误，response body --> " + JSONUtil.toJSONString(body));
                }
                return body.get("access_token");
            });
        } catch (ExecutionException e) {
            throw new HttpRequestException("调用微信公众号接口错误，获取access_token失败", e);
        }
    }

    public SNSToken getSNSToken(String code) {
        if (Objects.isNull(code)) {
            throw new HttpRequestException("获取微信公众号网页授权失败，code is null");
        }
        try {
            Response<SNSToken> response = api.getSNSToken(
                    WechatOfficialAPI.SNS_GRANT_TYPE,
                    config.getAppId(),
                    config.getAppSecret(),
                    code
            ).execute();
            SNSToken body = response.body();
            if (Objects.isNull(body) || Objects.isNull(body.getOpenid())) {
                throw new HttpRequestException("获取微信公众号网页授权失败，未获取到用户信息 --> " + JSONUtil.toJSONString(body));
            }
            return body;
        } catch (IOException e) {
            throw new HttpRequestException("获取微信公众号网页授权失败", e);
        }
    }
}
