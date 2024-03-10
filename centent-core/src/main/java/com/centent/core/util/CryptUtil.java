package com.centent.core.util;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.util.DigestUtils;

/**
 * 数据加解密相关工具类
 *
 * @since 0.0.1
 */
@Slf4j
public class CryptUtil {

    /**
     * 计算文本的md5值
     *
     * @param text 目标文本
     * @return md5值
     * @since 0.0.1
     */
    public static String md5(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes());
    }


    /**
     * BCrypt加密
     *
     * @param text 待加密的文本
     * @return 加密数据
     * @since 0.0.1
     */
    public static String jbEncrypt(String text) {
        return BCrypt.hashpw(text, BCrypt.gensalt());
    }

    /**
     * BCrypt校验
     *
     * @param text   待验证的文本
     * @param hashed 校验数据
     * @return 校验结果
     * @since 0.0.1
     */
    public static boolean jbVerify(String text, String hashed) {
        return BCrypt.checkpw(text, hashed);
    }

}
