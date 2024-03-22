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

    private String manufactureDate;

    private String carColor;

    private String limitPassenger;

    private String engineType;

    private String totalWeight;

    private String power;

    private String certificationNo;

    private String fuelType;

    private String manufacturer;

    private String steeringType;

    private String wheelbase;

    private String speedLimit;

    private String engineNo;

    private String saddleMass;

    private String axleNum;

    private String carModel;

    private String vin;

    private String carBrand;

    private String emissionStandard;

    private String displacement;

    private String certificateDate;

    private String carName;

    private String tyreNum;

    private String chassisId;

    private String chassisModel;

    private String seatingCapacity;

    private String qualifySeal;

    private String cgsSeal;
}
