package com.centent.ocr.bean;

import com.google.common.base.Strings;
import lombok.Data;

import java.util.Objects;

@Data
public class Idcard {

    public static final String LONG_TERM = "长期";

    /**
     * ID
     *
     * @since 0.0.1
     */
    private String id;

    /**
     * 证件号码
     *
     * @since 0.0.1
     */
    private String number;

    /**
     * 姓名
     *
     * @since 0.0.1
     */
    private String name;

    /**
     * 性别
     *
     * @since 0.0.1
     */
    private String gender;

    /**
     * 详细地址
     *
     * @since 0.0.1
     */
    private String address;

    /**
     * 出生日期
     *
     * @since 0.0.1
     */
    private String birthDay;

    /**
     * 民族
     *
     * @since 0.0.1
     */
    private String nation;

    /**
     * 签发机关
     *
     * @since 0.0.1
     */
    private String issuingAuthority;

    /**
     * 签发日期
     *
     * @since 0.0.1
     */
    private String issuingDate;

    /**
     * 过期日期，日期或者“长期”
     *
     * @since 0.0.1
     */
    private String expirationDate;

    /**
     * 是否为长期身份证
     *
     * @return true or false
     * @since 0.0.1
     */
    public boolean isLongTerm() {
        if (Strings.isNullOrEmpty(expirationDate)) {
            return true;
        }
        return Objects.equals(LONG_TERM, expirationDate);
    }
}
