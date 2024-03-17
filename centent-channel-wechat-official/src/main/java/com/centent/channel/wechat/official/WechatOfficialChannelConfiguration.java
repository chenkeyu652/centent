package com.centent.channel.wechat.official;

import com.centent.channel.wechat.official.config.WechatOfficialConfig;
import com.centent.channel.wechat.official.retrofit.WechatOfficialAPI;
import com.centent.core.retrofit.IRetrofitDefine;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

@Slf4j
@Configuration
public class WechatOfficialChannelConfiguration implements IRetrofitDefine {

    @Resource
    private WechatOfficialConfig config;

    private Retrofit retrofit;

    @PostConstruct
    public void init() {
        log.info("channel-wechat-official baseUrl: {}", config.getUrl());
        this.retrofit = this.buildRetrofit();
    }

    @Override
    public String getBaseUrl() {
        return config.getUrl();
    }

    @Bean
    public WechatOfficialAPI wechatOfficialAPI() {
        log.info("register bean: wechatOfficialAPI");
        return this.retrofit.create(WechatOfficialAPI.class);
    }
}
