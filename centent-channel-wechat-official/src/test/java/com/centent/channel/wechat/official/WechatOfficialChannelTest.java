package com.centent.channel.wechat.official;

import com.centent.channel.NotifyContext;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class WechatOfficialChannelTest {

    @Resource
    private WechatOfficialChannel channel;

    @Test
    void sendNotify() {
        String target = "o31096zAvrsUPDgdGbyRUPc1s25Q";
        String url = "https://mobileshare.chinahuanong.com.cn/index.html#/home?proposalNo=UTUxOTUwNTA5MjAyNDAxMTc3MA==&operatorCode=NTE0MDI2ODM=&poaType=&time={time}";
        Map<String, String> notifyData = Map.of(
                "title", "订单审核通过，请完成支付", // 微信公众号渠道用不到这个
                "url", url,  // 跳转页面
                "pay_url", url // 付款二维码
        );
        channel.sendNotify(new NotifyContext("notifyPaying", target, notifyData));
    }
}