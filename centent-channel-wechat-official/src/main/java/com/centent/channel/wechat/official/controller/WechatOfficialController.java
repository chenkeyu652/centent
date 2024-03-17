package com.centent.channel.wechat.official.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/wechat/official")
public class WechatOfficialController {

    /**
     * @param signature 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param echostr   随机字符串
     * @return 将入参echostr响应给微信
     * @since 0.0.1
     */
    @GetMapping("api-verify")
    public String verify(String signature,
                         String timestamp,
                         String nonce,
                         String echostr) {
        return echostr;
    }
}
