package com.centent.ocr.bean;

import com.centent.data.division.bean.Region;
import com.google.common.base.Strings;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class Idcard implements OCRResult {

    public static final String LONG_TERM = "长期";

    /**
     * 文件ID
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
     * 当前行政区划代码
     *
     * @since 0.0.1
     */
    private List<Region> regions = new ArrayList<>();

    /**
     * 详细地址（不包含省市区）
     *
     * @since 0.0.1
     */
    private String addressLast;

    // ========================================= 以上是身份证（人像面）信息 =========================================
    // ========================================= 以下是身份证（国徽面）信息 =========================================

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
