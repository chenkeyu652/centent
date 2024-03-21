package com.centent.data.division;


import com.baomidou.mybatisplus.annotation.TableName;
import com.centent.core.define.BaseEntity;
import com.centent.data.division.bean.Region;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * 中华人民共和国行政区划表 - 身份证地址码表
 *
 * @since 0.0.1
 */
@Data
@TableName("identity_address")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"current"})
public class IdentityAddress extends BaseEntity {

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
    @JsonIgnore
    private transient IdentityAddress city;

    /**
     * 省级行政区划
     *
     * @since 0.0.1
     */
    @JsonIgnore
    private transient IdentityAddress province;

    /**
     * 新的行政区划
     *
     * @since 0.0.1
     */
    @JsonIgnore
    private transient Set<IdentityAddress> next;

    /**
     * 当前行政区划
     *
     * @since 0.0.1
     */
    @JsonIgnore
    private transient Set<Region> current = new HashSet<>();
}
