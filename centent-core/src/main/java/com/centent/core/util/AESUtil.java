package com.centent.core.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class AESUtil {

    private static final int KEY_SIZE = 128;
    /**
     * 在一般情况下，推荐使用CBC模式而不是ECB模式进行加密。
     * 这是因为ECB模式存在一些安全性方面的缺陷，尤其是对于相同的明文块会得到相同的密文块，这可能会导致一些安全性问题。
     * 而CBC模式通过使用初始化向量（IV）来增加加密的随机性，从而提高了安全性。
     */
    public static String MODE = "AES/CBC/PKCS5Padding";
    public static String KEY_ALGORITHM = "AES";

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

    public static String encrypt(String key, String iv, String content) {
        return encrypt(key, iv, content.getBytes(StandardCharsets.UTF_8));
    }

    public static String encrypt(String key_iv, String content) {
        String[] keyIV = key_iv.split("_");
        return encrypt(keyIV[0], keyIV[1], content);
    }

    public static String encrypt(String key_iv, byte[] content) {
        String[] keyIV = key_iv.split("_");
        return encrypt(keyIV[0], keyIV[1], content);
    }

    public static String encrypt(String key, String iv, byte[] content) {
        byte[] encrypt = encrypt(content, key, iv);
        // 这一步非必须，是因为二进制数组不方便传输，所以加密的时候才进行base64编码
        encrypt = Base64.getEncoder().encode(encrypt);
        // 转成字符串返回
        return new String(encrypt, StandardCharsets.UTF_8);
    }

    /**
     * 加密
     *
     * @param content 内容
     * @param key     秘钥
     * @return 加密后的数据
     */
    public static byte[] encrypt(byte[] content, String key, String iv) {
        try {
            // 新建Cipher 类
            Cipher cipher = Cipher.getInstance(MODE);
            // 初始化秘钥
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            IvParameterSpec IV = new IvParameterSpec(iv.getBytes());

            // 初始化加密类
            cipher.init(Cipher.ENCRYPT_MODE, spec, IV);
            // 进行加密
            return cipher.doFinal(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String key, String iv, String content) {
        content = content.replaceAll("[\\n\\r]", "");
        return decrypt(key, iv, Base64.getDecoder().decode(content));
    }

    public static String decrypt(String key, String iv, byte[] content) {
        byte[] decrypt = decrypt(content, key, iv);
        // 返回解密后的内容
        return new String(decrypt, StandardCharsets.UTF_8);
    }

    public static byte[] decrypt(String key_iv, String content) {
        String[] keyIV = key_iv.split("_");
        content = content.replaceAll("[\\n\\r]", "");
        return decrypt(Base64.getDecoder().decode(content), keyIV[0], keyIV[1]);
    }

    /**
     * 解密数据
     *
     * @param key     秘钥
     * @param content 内容
     * @return 数据
     */
    public static byte[] decrypt(byte[] content, String key, String iv) {
        try {
            // 新建Cipher 类
            Cipher cipher = Cipher.getInstance(MODE);
            // 初始化秘钥
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            IvParameterSpec IV = new IvParameterSpec(iv.getBytes());
            // 初始化类
            cipher.init(Cipher.DECRYPT_MODE, spec, IV);
            // 解密
            return cipher.doFinal(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String plainText = "我是文本1";
        log.info("加密前的明文: {}", plainText);
        String key = MessageDigestUtil.bytesToHexString(getKey().getEncoded());
        String iv = "b65e9b0b574bb093";
        // aesKey = "8cc3c34dc7a362f7146a54c042ebe509cd41c713d3fd4ae7b26dd41e04b44a95";
        log.info("AES密钥: {}", key);
        log.info("AES-IV: {}", iv);
        String cipherText = encrypt(key, iv, plainText);
        log.info("加密后的密文: {}", cipherText);
        String plainText2 = decrypt(key, iv, cipherText);
        log.info("解密后的明文: {}", plainText2);
    }
}
