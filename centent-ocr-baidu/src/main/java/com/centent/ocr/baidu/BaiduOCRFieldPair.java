package com.centent.ocr.baidu;

import com.centent.ocr.bean.Idcard;
import com.centent.ocr.bean.VehicleCertificate;
import com.centent.ocr.bean.VehicleLicence;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.function.BiConsumer;

public class BaiduOCRFieldPair {

    static final Map<String, BiConsumer<Idcard, String>> IDCARD_PAIRS = ImmutableMap.<String, BiConsumer<Idcard, String>>builder()
            .put("公民身份号码", Idcard::setNumber)
            .put("姓名", Idcard::setName)
            .put("性别", Idcard::setGender)
            .put("住址", Idcard::setAddress)
            .put("出生", Idcard::setBirthDay)
            .put("民族", Idcard::setNation)
            .put("签发机关", Idcard::setIssuingAuthority)
            .put("签发日期", Idcard::setIssuingDate)
            .put("失效日期", Idcard::setExpirationDate)
            .build();

    static final Map<String, BiConsumer<VehicleLicence, String>> VEHICLE_LICENCE_PAIRS = ImmutableMap.<String, BiConsumer<VehicleLicence, String>>builder()
            .put("车辆识别代号", VehicleLicence::setVin)
            .put("住址", VehicleLicence::setAddress)
            .put("发证单位", VehicleLicence::setIssuingAuthority)
            .put("发证日期", VehicleLicence::setIssuingDate)
            .put("品牌型号", VehicleLicence::setModel)
            .put("车辆类型", VehicleLicence::setVehicleType)
            .put("所有人", VehicleLicence::setOwner)
            .put("使用性质", VehicleLicence::setUseCharacter)
            .put("发动机号码", VehicleLicence::setEngineNo)
            .put("号牌号码", VehicleLicence::setPlateNo)
            .put("注册日期", VehicleLicence::setRegisterDate)
            // 以下是行驶证（副页）信息
            .put("检验记录", VehicleLicence::setInspectionRecord)
            .put("核定载质量", VehicleLicence::setApprovedLoad)
            .put("整备质量", VehicleLicence::setUnladenMass)
            .put("外廓尺寸", VehicleLicence::setOverallDimension)
            .put("核定载人数", VehicleLicence::setPassengersCapacity)
            .put("总质量", VehicleLicence::setGrossMass)
            .put("燃油类型", VehicleLicence::setFuelType)
            .put("准牵引总质量", VehicleLicence::setTractionMass)
            .put("备注", VehicleLicence::setComment)
            .put("档案编号", VehicleLicence::setFileNo)
            .put("证芯编号", VehicleLicence::setBarcode)
            .build();

    static final Map<String, BiConsumer<VehicleCertificate, String>> VEHICLE_CERTIFICATE_PAIRS = ImmutableMap.<String, BiConsumer<VehicleCertificate, String>>builder()
            .put("ManufactureDate", VehicleCertificate::setManufactureDate)
            .put("CarColor", VehicleCertificate::setCarColor)
            .put("LimitPassenger", VehicleCertificate::setLimitPassenger)
            .put("EngineType", VehicleCertificate::setEngineType)
            .put("TotalWeight", VehicleCertificate::setTotalWeight)
            .put("Power", VehicleCertificate::setPower)
            .put("CertificationNo", VehicleCertificate::setCertificationNo)
            .put("FuelType", VehicleCertificate::setFuelType)
            .put("Manufacturer", VehicleCertificate::setManufacturer)
            .put("SteeringType", VehicleCertificate::setSteeringType)
            .put("Wheelbase", VehicleCertificate::setWheelbase)
            .put("SpeedLimit", VehicleCertificate::setSpeedLimit)
            .put("EngineNo", VehicleCertificate::setEngineNo)
            .put("SaddleMass", VehicleCertificate::setSaddleMass)
            .put("AxleNum", VehicleCertificate::setAxleNum)
            .put("CarModel", VehicleCertificate::setCarModel)
            .put("VinNo", VehicleCertificate::setVin)
            .put("CarBrand", VehicleCertificate::setCarBrand)
            .put("EmissionStandard", VehicleCertificate::setEmissionStandard)
            .put("Displacement", VehicleCertificate::setDisplacement)
            .put("CertificateDate", VehicleCertificate::setCertificateDate)
            .put("CarName", VehicleCertificate::setCarName)
            .put("TyreNum", VehicleCertificate::setTyreNum)
            .put("ChassisID", VehicleCertificate::setChassisId)
            .put("ChassisModel", VehicleCertificate::setChassisModel)
            .put("SeatingCapacity", VehicleCertificate::setSeatingCapacity)
            .put("QualifySeal", VehicleCertificate::setQualifySeal)
            .put("CGSSeal", VehicleCertificate::setCgsSeal)
            .build();
}
