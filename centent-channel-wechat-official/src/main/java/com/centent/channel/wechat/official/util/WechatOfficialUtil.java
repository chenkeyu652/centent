package com.centent.channel.wechat.official.util;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WechatOfficialUtil {

    /**
     * 验证微信签名
     *
     * @since 0.0.1
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce, String token) throws NoSuchAlgorithmException {
        // 将token、timestamp、nonce三个参数进行字典序排序
        String[] arr = new String[]{token, timestamp, nonce};
        Arrays.sort(arr);

        // 将三个参数字符串拼接成一个字符串进行sha1加密
        String content = arr[0] + arr[1] + arr[2];
        byte[] digest = MessageDigest.getInstance("SHA-1").digest(content.getBytes());
        String tmpStr = byteToStr(digest);
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return tmpStr.equals(signature.toUpperCase());
    }

    /**
     * 构建普通消息
     *
     * @param toUser     接收方账号（openId）
     * @param fromUser   开发者账号
     * @param createTime 创建时间，整型
     * @param content    内容
     * @return 回复消息
     * @since 0.0.1
     */
    public static String buildMessage(String toUser, String fromUser, Long createTime, String content) {
        return "<xml>\n" +
                "  <ToUserName><![CDATA[" + toUser + "]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[" + fromUser + "]]></FromUserName>\n" +
                "  <CreateTime>" + createTime + "</CreateTime>\n" +
                "  <MsgType><![CDATA[text]]></MsgType>\n" +
                "  <Content><![CDATA[" + content + "]]></Content>\n" +
                "</xml>";
    }

    private static String byteToStr(byte[] byteArray) {
        StringBuilder strDigest = new StringBuilder();
        for (byte b : byteArray) {
            strDigest.append(byteToHexStr(b));
        }
        return strDigest.toString();
    }

    private static String byteToHexStr(byte mByte) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];
        return new String(tempArr);
    }

    /**
     * xml文件中的数据转换我Map<String,Object>中的数据
     *
     * @param xml xml格式的字符串
     * @return Map<String, String>
     * @throws Exception Exception
     */
    public static Map<String, String> parseXml(String xml) throws Exception {
        Map<String, String> map = new HashMap<>();

        byte[] xmlBytes = xml.getBytes(StandardCharsets.UTF_8);
        try (InputStream is = new ByteArrayInputStream(xmlBytes)) {
            // 这里用Dom的方式解析回包的最主要目的是防止API新增回包字段
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(is);

            // 获取到document里面的全部结点
            NodeList allNodes = document.getFirstChild().getChildNodes();
            for (int i = 0; i < allNodes.getLength(); i++) {
                Node node = allNodes.item(i);
                if (node instanceof Element) {
                    map.put(node.getNodeName(), node.getTextContent());
                }
            }
        }
        return map;
    }
}
