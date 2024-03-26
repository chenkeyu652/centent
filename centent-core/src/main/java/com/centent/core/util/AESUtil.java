package com.centent.core.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class AESUtil {

    public static String MODE = "AES/ECB/PKCS5Padding";

    public static String KEY_ALGORITHM = "AES";

    private static final int KEY_SIZE = 128;

    /**
     * 获取密钥
     *
     * @return AES密钥
     */
    private static Key getKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            kg.init(KEY_SIZE);
            return kg.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加密
     *
     * @param key     秘钥
     * @param content 内容
     * @return 加密后的数据
     */
    public static String encrypt(String key, String content) {
        try {
            // 新建Cipher 类
            Cipher cipher = Cipher.getInstance(MODE);
            // 初始化秘钥
            SecretKeySpec sks = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            // 初始化加密类
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            // 进行加密
            byte[] encrypt = cipher.doFinal(content.getBytes());
            // 这一步非必须，是因为二进制数组不方便传输，所以加密的时候才进行base64编码
            encrypt = Base64.getEncoder().encode(encrypt);
            // 转成字符串返回
            return new String(encrypt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密数据
     *
     * @param key     秘钥
     * @param content 内容
     * @return 数据
     */
    public static String decrypt(String key, String content) {
        try {
            // 替换base64里的换行,这一步也非必须，只是有些情况base64里会携带换行符导致解码失败
            content = content.replaceAll("[\\n\\r]", "");
            // base64 解码，跟上面的编码对称
            byte[] data = Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8));
            // 新建Cipher 类
            Cipher cipher = Cipher.getInstance(MODE);
            // 初始化秘钥
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            // 初始化类
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            // 解密
            byte[] result = cipher.doFinal(data);
            // 返回解密后的内容
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String plainText = "大叔大婶大家倒萨的！@@#￥";
        log.info("加密前的明文: {}", plainText);
        String aesKey = Base64.getEncoder().encodeToString(getKey().getEncoded());
        log.info("AES密钥: {}", aesKey);
        String cipherText = encrypt(aesKey, plainText);
        log.info("加密后的密文: {}", cipherText);
        String plainText2 = decrypt(aesKey, cipherText);
        log.info("解密后的明文: {}", plainText2);
    }
}
