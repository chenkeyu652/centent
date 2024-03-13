package com.centent.ocr.baidu;

import com.centent.core.exception.HttpRequestException;
import com.centent.core.util.CententUtil;
import com.centent.core.util.JSONUtil;
import com.centent.ocr.IOCR;
import com.centent.ocr.baidu.config.BaiduOCRConfig;
import com.centent.ocr.baidu.retrofit.BaiduOCRAPI;
import com.centent.ocr.bean.Idcard;
import com.centent.ocr.bean.VehicleLicence;
import com.centent.ocr.enums.Direction;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import retrofit2.Response;

import java.io.IOException;
import java.util.Map;

public class BaiduOCR extends IOCR {

    @Resource
    private BaiduOCRAPI baiduOCRAPI;

    @Resource
    private BaiduOCRConfig config;

    @Override
    public Idcard idcard(String base64, Direction direction) {
        String accessToken = this.getToken();
        String idCardSide = direction == Direction.FRONT ? "front" : "back";
        try {
            Response<Map<String, Object>> response = baiduOCRAPI.idcard(accessToken,
                            idCardSide,
                            base64,
                            null,
                            config.getIdcard().isDetectRisk(),
                            config.getIdcard().isDetectQuality(),
                            config.getIdcard().isDetectPhoto(),
                            config.getIdcard().isDetectCard(),
                            config.getIdcard().isDetectDirection())
                    .execute();
            Map<String, Object> body = response.body();
            if (CollectionUtils.isEmpty(body) || !body.containsKey("words_result")) {
                throw new HttpRequestException("调用百度身份证OCR错误，response body is empty --> " + JSONUtil.toJSONString(body));
            }

            Idcard idcard = new Idcard();
            body.forEach((key, value) -> {
                if (BaiduOCRFieldPair.IDCARD_PAIRS.containsKey(key) && CententUtil.initialized(value)) {
                    CardEntity entity = JSONUtil.json2Object(value.toString(), CardEntity.class);
                    assert entity != null;
                    BaiduOCRFieldPair.IDCARD_PAIRS.get(key).accept(idcard, entity.getWords());
                }
            });

            return idcard;
        } catch (Exception e) {
            throw new HttpRequestException("调用百度OCR错误，身份证识别失败", e);
        }
    }

    @Override
    public VehicleLicence vehicleLicence(String base64, Direction direction) {
        String accessToken = this.getToken();
        String idCardSide = direction == Direction.FRONT ? "front" : "back";

        try {
            Response<Map<String, Object>> response = baiduOCRAPI.vehicleLicence(accessToken,
                            idCardSide,
                            base64,
                            null,
                            config.getVehicleLicence().isDetectDirection(),
                            config.getVehicleLicence().isUnified(),
                            config.getVehicleLicence().isQualityWarn(),
                            config.getVehicleLicence().isRiskWarn())
                    .execute();
            Map<String, Object> body = response.body();
            if (CollectionUtils.isEmpty(body) || !body.containsKey("words_result")) {
                throw new HttpRequestException("调用百度行驶证OCR错误，response body is empty --> " + JSONUtil.toJSONString(body));
            }

            VehicleLicence vehicleLicence = new VehicleLicence();
            body.forEach((key, value) -> {
                if (BaiduOCRFieldPair.VEHICLE_LICENCE_PAIRS.containsKey(key) && CententUtil.initialized(value)) {
                    CardEntity entity = JSONUtil.json2Object(value.toString(), CardEntity.class);
                    assert entity != null;
                    BaiduOCRFieldPair.VEHICLE_LICENCE_PAIRS.get(key).accept(vehicleLicence, entity.getWords());
                }
            });

            return vehicleLicence;
        } catch (IOException e) {
            throw new HttpRequestException("调用百度OCR错误，行驶证识别失败", e);
        }
    }

    private String getToken() {
        try {
            Response<Map<String, String>> response = baiduOCRAPI.getToken(BaiduOCRAPI.GRANT_TYPE, config.getApiKey(), config.getSecretKey())
                    .execute();
            Map<String, String> body = response.body();
            if (CollectionUtils.isEmpty(body) || !body.containsKey("access_token")) {
                throw new HttpRequestException("调用百度OCR错误，获取access_token失败，response body: " + JSONUtil.toJSONString(body));
            }
            return body.get("access_token");
        } catch (Exception e) {
            throw new HttpRequestException("调用百度OCR错误，获取access_token失败", e);
        }
    }

    @Data
    private static class CardEntity {

        /**
         * 识别结果
         *
         * @since 0.0.1
         */
        private String words;

        /**
         * 坐标信息
         *
         * @since 0.0.1
         */
        private Map<String, Integer> location;
    }
}
