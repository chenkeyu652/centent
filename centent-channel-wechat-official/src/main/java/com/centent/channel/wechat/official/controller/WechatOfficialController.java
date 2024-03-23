package com.centent.channel.wechat.official.controller;

import com.centent.channel.wechat.official.config.WechatOfficialConfig;
import com.centent.channel.wechat.official.service.WechatOfficialService;
import com.centent.channel.wechat.official.util.WechatOfficialUtil;
import com.centent.core.exception.BusinessException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

@Slf4j
@RestController
@RequestMapping("/centent/wechat/official")
public class WechatOfficialController {

    @Resource
    private WechatOfficialConfig config;

    @Resource
    private WechatOfficialService service;

    @RequestMapping(value = "/receive", method = {RequestMethod.GET, RequestMethod.POST})
    public String receive(HttpServletRequest request) throws NoSuchAlgorithmException {
        String method = request.getMethod();
        if (Objects.equals(method, RequestMethod.GET.name())) {
            String signature = request.getParameter("signature");
            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            String echoStr = request.getParameter("echostr");
            boolean checked = WechatOfficialUtil.checkSignature(signature, timestamp, nonce, config.getApiToken());
            log.debug("接收到来自微信服务器的Token验证请求，验证结果：{}, signature：{}, timestamp：{}, nonce：{}",
                    checked, signature, timestamp, nonce);
            return checked ? echoStr : "failed";
        } else {
            //post请求跟消息相关的逻辑
            String body = null;
            try {
                body = this.readRequestBody(request);
                Map<String, String> map = WechatOfficialUtil.parseXml(body);
                if (CollectionUtils.isEmpty(map)) {
                    throw new BusinessException("没有解析到有效的xml数据");
                }
                // 处理微信事件类型消息
                if (Objects.equals(map.get("MsgType"), "event")) {
                    EventType type = EventType.getEventType(map.get("Event"));
                    if (Objects.nonNull(type)) {
                        type.getConsumer().accept(service, map);
                    }
                }
            } catch (Exception e) {
                log.error("处理微信消息异常, body=" + body, e);
            }
            return "success";
        }
    }


    /**
     * 读取 request body 内容作为字符串
     *
     * @param request HttpServletRequest
     * @return request body
     * @throws IOException io异常
     */
    public String readRequestBody(HttpServletRequest request) throws IOException {
        InputStream inputStream;
        StringBuilder sb = new StringBuilder();
        inputStream = request.getInputStream();
        String str;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        while ((str = in.readLine()) != null) {
            sb.append(str);
        }
        in.close();
        inputStream.close();
        return sb.toString();
    }

    @Getter
    private enum EventType {

        subscribe(WechatOfficialService::subscribe),

        unsubscribe(WechatOfficialService::unsubscribe);

        private final BiConsumer<WechatOfficialService, Map<String, String>> consumer;

        EventType(BiConsumer<WechatOfficialService, Map<String, String>> consumer) {
            this.consumer = consumer;
        }

        public static EventType getEventType(String event) {
            for (EventType type : EventType.values()) {
                if (Objects.equals(type.name(), event)) {
                    return type;
                }
            }
            return null;
        }
    }
}