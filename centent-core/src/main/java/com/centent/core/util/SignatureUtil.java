package com.centent.core.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class SignatureUtil {
    private static final String HMAC_SHA1 = "HmacSHA1";

    private static final String HMAC_SHA256 = "HmacSHA256";

    public static String sign(String algorithm, String secret, String message, boolean encode) {
        try {
            Mac hmac = Mac.getInstance(algorithm);
            hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), algorithm));
            byte[] bytes = hmac.doFinal(message.getBytes());
            if (encode) {
                return Base64.getEncoder().encodeToString(bytes).toUpperCase();
            }
            return MessageDigestUtil.bytesToHexString(bytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static String signSHA1(String secret, String message, boolean encode) {
        return sign(HMAC_SHA1, secret, message, encode);
    }

    public static String signSHA1(String secret, String message) {
        return sign(HMAC_SHA1, secret, message, Boolean.TRUE);
    }

    public static String signSHA256(String secret, String message, boolean encode) {
        return sign(HMAC_SHA256, secret, message, encode);
    }

    public static String signSHA256(String secret, String message) {
        return sign(HMAC_SHA256, secret, message, Boolean.TRUE);
    }

    public static boolean valid(String algorithm, String secret, String message, String signature, boolean encode) {
        return signature != null && signature.equals(sign(algorithm, secret, message, encode));
    }

    public static boolean validSHA1(String secret, String message, String signature, boolean encode) {
        return signature != null && signature.equals(signSHA1(secret, message, encode));
    }

    public static boolean validSHA256(String secret, String message, String signature, boolean encode) {
        return signature != null && signature.equals(signSHA256(secret, message, encode));
    }

    public static void main(String[] args) {
        String appSecret = "b06c75b58d1701ff470119a4114f8b45";
        String appId = "10000001";
        String timestamp = "1711506101887";

        String message = appId + timestamp + "我是一段文本，还包含一些特殊字符：~@#￥%……&*（）——+$,./<>?;':\\||//{}|[]``\"\ndsd\t没了";
        System.out.println("加密前 =========>" + message);
        try {
            String str = signSHA256(appSecret, message, Boolean.TRUE);
            System.out.println("加密后 =========>" + str);
            System.out.println("验证签名 =========>" + validSHA256(appSecret, message, str, Boolean.TRUE));
        } catch (Exception e) {
            System.out.println("Error HmacSHA256 ===========>" + e.getMessage());
        }

    }
}
