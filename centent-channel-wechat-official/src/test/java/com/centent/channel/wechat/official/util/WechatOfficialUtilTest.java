package com.centent.channel.wechat.official.util;

import com.centent.core.util.JSONUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

class WechatOfficialUtilTest {

    @Test
    void parseXml() throws Exception {
        String example = """
                <xml>
                    <ToUserName><![CDATA[toUser]]></ToUserName>
                    <FromUserName><![CDATA[fromUser]]></FromUserName>
                    <CreateTime>1348831860</CreateTime>
                    <MsgType><![CDATA[text]]></MsgType>
                    <Content><![CDATA[this is a test]]></Content>
                    <MsgId>1234567890123456</MsgId>
                </xml>
                """;
        Map<String, String> map = WechatOfficialUtil.parseXml(example);
        System.out.println(JSONUtil.toJSONString(map));
    }
}