package com.centent.ocr.baidu;

import com.centent.core.retrofit.IRetrofitDefine;
import com.centent.ocr.IOCR;
import com.centent.ocr.baidu.config.BaiduOCRConfig;
import com.centent.ocr.baidu.retrofit.BaiduOCRAPI;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "centent.ocr.service", havingValue = "com.centent.ocr.baidu.BaiduOCR", matchIfMissing = true)
public class BaiduOCRConfiguration implements IRetrofitDefine {

    @Resource
    private BaiduOCRConfig config;

    private Retrofit retrofit;

    @PostConstruct
    public void init() {
        log.info("ocr-baidu init baseUrl:{}", config.getUrl());
        this.retrofit = this.buildRetrofit();
    }

    @Override
    public String getBaseUrl() {
        return config.getUrl();
    }

    @Bean
    public IOCR baiduOCR() {
        log.info("register bean: baiduOCR");
        return new BaiduOCR();
    }

    @Bean
    public BaiduOCRAPI baiduOCRRAPI() {
        log.info("register bean: baiduOCRRAPI");
        return this.retrofit.create(BaiduOCRAPI.class);
    }
}
