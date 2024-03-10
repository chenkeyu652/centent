package com.centent.core.define;

/**
 * 数据签名接口
 *
 * @since 0.0.1
 */
public interface ISign {

    /**
     * 调用此方法计算数据签名
     *
     * @param time 13位时间戳
     * @param key  密钥
     * @return 计算出来的签名数据，用于验签
     * @since 0.0.1
     */
    public abstract String calculateSign(String time, String key);
}
