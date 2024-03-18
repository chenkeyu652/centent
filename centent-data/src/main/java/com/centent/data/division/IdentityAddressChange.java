package com.centent.data.division;


import com.baomidou.mybatisplus.annotation.TableName;
import com.centent.core.define.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 中华人民共和国行政区划表 - 全量变化数据
 *
 * @since 0.0.1
 */
@Data
@TableName("identity_address_change")
@EqualsAndHashCode(callSuper = true)
public class IdentityAddressChange extends BaseEntity<IdentityAddressChange> {

    /**
     * 原行政区划代码
     *
     * @since 0.0.1
     */
    private Integer code;

    /**
     * 新行政区划代码
     */
    private Integer newCode;

    /**
     * 变化年份
     */
    private Integer time;
}
