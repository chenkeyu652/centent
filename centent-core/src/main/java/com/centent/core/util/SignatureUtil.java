package com.centent.core.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class SignatureUtil {
    private static final String HMAC_SHA1 = "HmacSHA1";

    private static final String HMAC_SHA256 = "HmacSHA256";

    public static String sign(String algorithm, String secret, String message) {
        try {
            Mac hmac = Mac.getInstance(algorithm);
            hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), algorithm));
            byte[] bytes = hmac.doFinal(message.getBytes());
            return MessageDigestUtil.bytesToHexString(bytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static String signSHA1(String secret, String message) {
        return sign(HMAC_SHA1, secret, message);
    }

    public static String signSHA256(String secret, String message) {
        return sign(HMAC_SHA256, secret, message);
    }

    public static boolean valid(String algorithm, String secret, String message, String signature) {
        return signature != null && signature.equals(sign(algorithm, secret, message));
    }

    public static boolean validSHA1(String secret, String message, String signature) {
        return signature != null && signature.equals(signSHA1(secret, message));
    }

    public static boolean validSHA256(String secret, String message, String signature) {
        return signature != null && signature.equals(signSHA256(secret, message));
    }

    public static void main(String[] args) {
        String appSecret = "b06c75b58d1701ff470119a4114f8b45";
        String appId = "10000001";
        String timestamp = "1529853639000";

        String message = appId + timestamp + "aaa";
        System.out.println("加密前 =========>" + message);
        try {
            String str = signSHA1(appSecret, message);
            System.out.println("加密后 =========>" + str);
            System.out.println("验证签名 =========>" + validSHA1(appSecret, message, str));
        } catch (Exception e) {
            System.out.println("Error HmacSHA256 ===========>" + e.getMessage());
        }

    }
}
