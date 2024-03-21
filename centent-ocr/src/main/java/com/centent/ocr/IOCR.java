package com.centent.ocr;

import com.centent.core.exception.BusinessException;
import com.centent.core.util.CententUtil;
import com.centent.core.util.FileUtil;
import com.centent.core.util.JSONUtil;
import com.centent.data.division.IdentityAddress;
import com.centent.data.division.IdentityAddressService;
import com.centent.ocr.bean.Idcard;
import com.centent.ocr.bean.VehicleLicence;
import com.centent.ocr.enums.Direction;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;

@Slf4j
public abstract class IOCR {

    @Resource
    private IdentityAddressService identityAddressService;

    /**
     * OCR系统名称
     *
     * @return OCR系统名称
     * @since 0.0.1
     */
    public abstract String name();

    public abstract Idcard idcard0(String base64, Direction direction);

    public abstract VehicleLicence vehicleLicence0(String base64, Direction direction);

    public final Idcard idcard(String base64, Direction direction) {
        Idcard idcard = this.idcard0(base64, direction);
        this.resolveIdcardAddress(idcard, direction);
        return idcard;
    }

    public final Idcard idcard(Direction direction, MultipartFile image) {
        String base64 = FileUtil.getFileAsBase64(image, false);
        return this.idcard(base64, direction);
    }

    public final Idcard idcard(Direction direction, File image) {
        String base64 = FileUtil.getFileAsBase64(image, false);
        return this.idcard(base64, direction);
    }

    public final Idcard idcard(Direction direction, String filePath) {
        String base64 = FileUtil.getFileAsBase64(filePath, false);
        return this.idcard(base64, direction);
    }

    public final VehicleLicence vehicleLicence(String base64, Direction direction) {
        VehicleLicence vehicleLicence = this.vehicleLicence0(base64, direction);
        // 品牌型号：识别为中文品牌+型号，去除中文字符，只留下型号
        CententUtil.executeIf(Strings::isNotBlank, vehicleLicence.getModel(),
                model -> vehicleLicence.setModelType(model.replaceAll("\\p{IsHan}", "")));
        return vehicleLicence;
    }

    public final VehicleLicence vehicleLicence(Direction direction, MultipartFile image) {
        String base64 = FileUtil.getFileAsBase64(image, false);
        return this.vehicleLicence(base64, direction);
    }

    public final VehicleLicence vehicleLicence(Direction direction, File image) {
        String base64 = FileUtil.getFileAsBase64(image, false);
        return this.vehicleLicence(base64, direction);
    }

    public final VehicleLicence vehicleLicence(Direction direction, String filePath) {
        String base64 = FileUtil.getFileAsBase64(filePath, false);
        return this.vehicleLicence(base64, direction);
    }

    // 根据身份证号码前6位解析身份证地址
    private void resolveIdcardAddress(Idcard idcard, Direction direction) {
        if (direction != Direction.BACK) {
            if (Strings.isBlank(idcard.getNumber())) {
                throw new BusinessException("身份证地址解析失败，身份证号码为空");
            }
            // 身份证号码+完整的身份证地址，较为精准的匹配行政区划
            String idAddress = idcard.getAddress();
            IdentityAddress area = identityAddressService.matchArea(idcard.getNumber(), idAddress);

            // 根据行政区划获取现行行政区划
            Optional.of(area)
                    .map(IdentityAddress::getCurrent)
                    .filter(c -> c.size() == 1)
                    .orElseThrow(() -> new BusinessException("身份证地址解析失败，匹配到多个现行行政区划：" + JSONUtil.toJSONString(area.getCurrent())));
            IdentityAddress current = area.getCurrent().iterator().next();

            idcard.setIdAddressArea(current.getName());
            idcard.setIdAddressCity(current.getCity().getName());
            idcard.setIdAddressProvince(current.getProvince().getName());
            if (idAddress.contains(current.getName())) {
                int index = idAddress.indexOf(current.getName()) + current.getName().length();
                idcard.setIdAddressLast(idAddress.substring(index));
            } else if (idAddress.contains(current.getCity().getName())) {
                int index = idAddress.indexOf(current.getCity().getName()) + current.getCity().getName().length();
                idcard.setIdAddressLast(idAddress.substring(index));
            } else {
                log.error("idAddressLast匹配失败，身份证完整地址为: {}, 解析出的行政区划信息为: {}", idAddress, JSONUtil.toJSONString(area));
            }
        }
    }
}
