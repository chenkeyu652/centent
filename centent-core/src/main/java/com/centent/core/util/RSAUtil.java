package com.centent.core.util;

import com.centent.core.bean.RSAKeyPair;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public class RSAUtil {

    /**
     * 算法名称
     */
    private static final String RSA = "RSA";

    /**
     * 密钥长度
     */
    private static final int KEY_SIZE = 2048;

    /**
     * keySize bit 数下的 RSA 密钥对所能够加密的最大明文大小。
     * RSA 算法一次能加密的明文长度与密钥长度(RSA 密钥对的 bit 数)成正比，
     * 默认情况下，Padding 方式为 OPENSSL_PKCS1_PADDING，RSA 算法会使
     * 用 11 字节的长度用于填充，所以默认情况下，RSA 所能够加密的最大明文大
     * 小为 (keySize / 8 - 11) byte
     */
    private static final int maxEncryptLength = 245;

    /**
     * keySize bit 数下的 RSA 密钥对所能够解密的最大密文大小。
     * (keySize / 8) byte
     */
    private static final int maxDecryptLength = 256;

    /**
     * 随机生成密钥对（包含公钥和私钥）
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance(RSA);
            gen.initialize(KEY_SIZE);
            return gen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 随机生成密钥对字符串（包含公钥和私钥）
     */
    public static RSAKeyPair generateRSAKeyPair() {
        KeyPair keyPair = generateKeyPair();
        return new RSAKeyPair(getPublicKeyStr(keyPair), getPrivateKeyStr(keyPair));
    }

    public static String getPrivateKeyStr(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    public static String getPublicKeyStr(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    /**
     * 使用指定的公钥进行 RSA 明文加密
     *
     * @param key       公钥
     * @param plainText 明文
     * @return 明文加密后的密文字符串
     */
    public static String encrypt(PublicKey key, String plainText) {
        // 字节数组输出流，用于拼接存储中间分段加密结果，最终生成明文的完整加密密文
        try (ByteArrayOutputStream cipherText = new ByteArrayOutputStream()) {
            // 获取要进行加密的明文的字节数组
            byte[] plainTextBytes = plainText.getBytes(StandardCharsets.UTF_8);
            // 明文的字节数组长度
            int plainTextBytesLen = plainTextBytes.length;
            // 计算明文需要分段加密的次数
            int encryptCount = ((int) Math.ceil((plainTextBytesLen * 1.0) / maxEncryptLength));
            // 字节数组输出流，用于拼接存储中间分段加密结果，最终生成明文的完整加密密文
            // 获取 RSA 加密算法对应的加密器
            Cipher rsaCipher = Cipher.getInstance(RSA);
            // 初始化加密器，指定加密器的工作模式为加密，以及加密的密钥
            rsaCipher.init(Cipher.ENCRYPT_MODE, key);
            // 进行明文的分段加密
            for (int i = 0; i < encryptCount; i++) {
                // 当前明文段距离开始位置的偏移量
                int offSet = i * maxEncryptLength;
                // 存储当前段明文加密后的密文
                byte[] cipherTextBytes;
                if (offSet + maxEncryptLength < plainTextBytesLen) {
                    // 需要进行加密的明文字节数组，偏移量，加密处理长度
                    cipherTextBytes = rsaCipher.doFinal(plainTextBytes, offSet, maxEncryptLength);
                } else {
                    cipherTextBytes = rsaCipher.doFinal(plainTextBytes, offSet, plainTextBytesLen - offSet);
                }
                // 存储当前明文段加密后的密文
                cipherText.write(cipherTextBytes, 0, cipherTextBytes.length);
            }
            // 将 RSA 加密后的字节数组形式的密文使用 BASE64 编码转换为字符串形式的密文
            return Base64.getEncoder().encodeToString(cipherText.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用 RSA 公钥对明文进行加密
     *
     * @param publicKey 公钥
     * @param plainText 明文
     * @return RSA 公钥加密后的密文
     */
    public static String encrypt(String publicKey, String plainText) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            PublicKey key = keyFactory.generatePublic(keySpec);
            return encrypt(key, plainText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用指定的私钥进行 RSA 密文解密
     *
     * @param key        密钥
     * @param cipherText 密文
     * @return 密文解密后的明文字符串
     */
    public static String decrypt(PrivateKey key, String cipherText) {
        // 字节数组输出流，用于拼接存储中间分段解密结果，最终生成密文的完整明文
        try (ByteArrayOutputStream plainText = new ByteArrayOutputStream()) {
            // 将被 BASE64 编码转为字符串的密文使用 BASE64 编码转换为字节数组形式的密文
            byte[] cipherTextBytes = Base64.getDecoder().decode(cipherText);
            // 字节数组形式的密文的长度
            int cipherTextBytesLen = cipherTextBytes.length;
            // 计算需要分段解码的次数
            int decryptCount = ((int) Math.ceil((cipherTextBytesLen * 1.0) / maxDecryptLength));
            // 字节数组输出流，用于拼接存储中间分段解密结果，最终生成密文的完整明文
            // 获取 RSA 加密算法对应的加密器
            Cipher rsaCipher = Cipher.getInstance(RSA);
            // 初始化加密器，指定加密器的工作模式为解密，以及解密的密钥
            rsaCipher.init(Cipher.DECRYPT_MODE, key);
            // 进行密文的分段解密
            for (int i = 0; i < decryptCount; i++) {
                // 当前明文段距离开始位置的偏移量
                int offSet = i * maxDecryptLength;
                // 存储当前段密文解密后的明文
                byte[] plainTextBytes;
                if (offSet + maxDecryptLength < cipherTextBytesLen) {
                    // 需要进行解密的密文字节数组，偏移量，解密处理长度
                    plainTextBytes = rsaCipher.doFinal(cipherTextBytes, offSet, maxDecryptLength);
                } else {
                    plainTextBytes = rsaCipher.doFinal(cipherTextBytes, offSet, cipherTextBytesLen - offSet);
                }
                // 存储当前密文段解密后的明文
                plainText.write(plainTextBytes, 0, plainTextBytes.length);
            }
            // 将 RSA 解密后的字节数组形式的明文转换为字符串形式的明文
            return plainText.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用 RSA 私钥对明文进行解密
     *
     * @param privateKey 私钥
     * @param plainText  明文
     * @return RSA 公钥加密后的密文
     */
    public static String decrypt(String privateKey, String plainText) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            PrivateKey key = keyFactory.generatePrivate(pkcs8KeySpec);
            return decrypt(key, plainText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        KeyPair keyPair = generateKeyPair();
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        log.info("私钥:{}", privateKey);
        log.info("公钥:{}", publicKey);
        String plainText = "大叔大婶大家倒萨的！@@#￥￥%%…………&&**（））+贺卡数据库的很健康ddfdsfsf";
        log.info("加密前的明文:{}", plainText);
        String cipherText = encrypt(publicKey, plainText);
        log.info("加密后的密文:{}", cipherText);
        String plainText2 = decrypt(privateKey, cipherText);
        log.info("解密后的明文:{}", plainText2);
    }
}
