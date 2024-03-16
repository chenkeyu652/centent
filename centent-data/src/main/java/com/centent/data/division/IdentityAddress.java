package com.centent.data.division;


import com.baomidou.mybatisplus.annotation.TableName;
import com.centent.core.define.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 中华人民共和国行政区划表 - 身份证地址码表
 *
 * @since 0.0.1
 */
@Data
@TableName("identity_address")
@EqualsAndHashCode(callSuper = true)
public class IdentityAddress extends BaseEntity<IdentityAddress> {

    /**
     * 行政区划代码
     *
     * @since 0.0.1
     */
    private Integer code;

    /**
     * 行政区划名称
     *
     * @since 0.0.1
     */
    private String name;

    /**
     * 启用年份
     *
     * @since 0.0.1
     */
    private Integer start;

    /**
     * 废止年份
     *
     * @since 0.0.1
     */
    private Integer ends;

    /**
     * 市级行政区划
     *
     * @since 0.0.1
     */
    private transient IdentityAddress city;

    /**
     * 省级行政区划
     *
     * @since 0.0.1
     */
    private transient IdentityAddress province;
}
