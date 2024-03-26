package com.centent.ocr.bean;

import lombok.Data;

@Data
public class VehicleCertificate implements OCRResult {

    /**
     * 文件ID
     *
     * @since 0.0.1
     */
    private String id;

    /**
     * 车辆制造日期
     */
    private String manufactureDate;

    /**
     * 车身颜色
     */
    private String carColor;

    /**
     * 驾驶室准乘人数
     */
    private String limitPassenger;

    /**
     * 发动机型号
     */
    private String engineType;

    /**
     * 总质量
     */
    private String totalWeight;

    /**
     * 功率
     */
    private String power;

    /**
     * 合格证编号
     */
    private String certificationNo;

    /**
     * 燃料种类
     */
    private String fuelType;

    /**
     * 车辆制造企业名
     */
    private String manufacturer;

    /**
     * 转向形式
     */
    private String steeringType;

    /**
     * 轴距
     */
    private String wheelbase;

    /**
     * 最高设计车速
     */
    private String speedLimit;

    /**
     * 发动机号
     */
    private String engineNo;

    /**
     * 整备质量
     */
    private String saddleMass;

    /**
     * 轴数
     */
    private String axleNum;

    /**
     * 车辆型号
     */
    private String carModel;

    /**
     * 车架号
     */
    private String vin;

    /**
     * 车辆品牌
     */
    private String carBrand;

    /**
     * 排放标准
     */
    private String emissionStandard;

    /**
     * 排量
     */
    private String displacement;

    /**
     * 发证日期
     */
    private String certificateDate;

    /**
     * 车辆名称
     */
    private String carName;

    /**
     * 轮胎数
     */
    private String tyreNum;

    /**
     * 底盘ID
     */
    private String chassisId;

    /**
     * 底盘型号
     */
    private String chassisModel;

    /**
     * 额定载客人数
     */
    private String seatingCapacity;

    /**
     * 合格印章：1表示有，0表示无
     */
    private String qualifySeal;

    /**
     * CGS印章：1表示有，0表示无
     */
    private String cgsSeal;
}
