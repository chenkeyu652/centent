package com.centent.core.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestUtil {

    /**
     * 使用指定哈希算法计算摘要信息
     *
     * @param content   内容
     * @param algorithm 哈希算法
     * @return 内容摘要
     */
    public static String getMD5Digest(String content, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(content.getBytes(StandardCharsets.UTF_8));
            return bytesToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param bytes 即将转换的数据
     * @return 16进制字符串
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length);
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(0xFF & aByte);
            if (temp.length() < 2) {
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }
}
