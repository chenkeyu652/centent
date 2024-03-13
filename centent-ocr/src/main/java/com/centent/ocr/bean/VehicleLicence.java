package com.centent.ocr.bean;

import lombok.Data;

@Data
public class VehicleLicence {

    /**
     * 车辆识别代号
     *
     * @since 0.0.1
     */
    private String vin;

    /**
     * 住址
     *
     * @since 0.0.1
     */
    private String address;

    /**
     * 发证单位
     *
     * @since 0.0.1
     */
    private String issuingAuthority;

    /**
     * 发证日期
     *
     * @since 0.0.1
     */
    private String issuingDate;

    /**
     * 品牌型号
     *
     * @since 0.0.1
     */
    private String model;

    /**
     * 车辆类型
     *
     * @since 0.0.1
     */
    private String vehicleType;

    /**
     * 所有人
     *
     * @since 0.0.1
     */
    private String owner;

    /**
     * 使用性质
     *
     * @since 0.0.1
     */
    private String useCharacter;

    /**
     * 发动机号码
     *
     * @since 0.0.1
     */
    private String engineNo;

    /**
     * 号牌号码
     *
     * @since 0.0.1
     */
    private String plateNo;

    /**
     * 注册日期
     *
     * @since 0.0.1
     */
    private String registerDate;

    // ========================================= 以上是行驶证（正页）信息 =========================================
    // ========================================= 以下是行驶证（副页）信息 =========================================

    /**
     * 检验记录
     *
     * @since 0.0.1
     */
    private String inspectionRecord;

    /**
     * 核定载质量
     *
     * @since 0.0.1
     */
    private String approvedLoad;

    /**
     * 整备质量
     *
     * @since 0.0.1
     */
    private String unladenMass;

    /**
     * 外廓尺寸
     *
     * @since 0.0.1
     */
    private String overallDimension;

    /**
     * 核定载人数
     *
     * @since 0.0.1
     */
    private String passengersCapacity;

    /**
     * 总质量
     *
     * @since 0.0.1
     */
    private String GrossMass;

    /**
     * 燃油类型
     *
     * @since 0.0.1
     */
    private String fuelType;

    /**
     * 准牵引总质量
     *
     * @since 0.0.1
     */
    private String tractionMass;

    /**
     * 备注
     *
     * @since 0.0.1
     */
    private String comment;

    /**
     * 档案编号
     *
     * @since 0.0.1
     */
    private String fileNo;

    /**
     * 证芯编号（条形码）
     *
     * @since 0.0.1
     */
    private String barcode;
}
