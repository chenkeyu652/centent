package com.centent.ocr.baidu;

import com.centent.cache.ICache;
import com.centent.core.exception.HttpRequestException;
import com.centent.core.util.CententUtil;
import com.centent.core.util.JSONUtil;
import com.centent.ocr.IOCR;
import com.centent.ocr.baidu.config.BaiduOCRConfig;
import com.centent.ocr.baidu.retrofit.BaiduOCRAPI;
import com.centent.ocr.bean.Idcard;
import com.centent.ocr.bean.VehicleCertificate;
import com.centent.ocr.bean.VehicleLicence;
import com.centent.ocr.enums.Direction;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import retrofit2.Response;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BaiduOCR extends IOCR {

    // 百度access token缓存前缀，access_token的有效期为30天，需要每30天进行定期更换 --- 官方文档说明
    // 见：https://ai.baidu.com/ai-doc/OCR/Ck3h7y2ia#%E8%B0%83%E7%94%A8%E6%96%B9%E5%BC%8F%E4%B8%80
    public static final String BAIDU_OCR_TOKEN_CACHE_KEY = "BAIDU_OCR_ACCESS_TOKEN";

    @Resource
    private BaiduOCRAPI api;

    @Resource
    private BaiduOCRConfig config;

    @Resource
    private ICache cache;

    @Override
    public String name() {
        return "百度OCR";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Idcard idcard0(String base64, Direction direction) {
        String accessToken = this.getToken();
        String cardSide = direction == Direction.FRONT ? "front" : "back";
        try {
            Response<Map<String, Object>> response = api.idcard(
                    accessToken,
                    config.getIdcard().isDetectRisk(),
                    config.getIdcard().isDetectQuality(),
                    config.getIdcard().isDetectPhoto(),
                    config.getIdcard().isDetectCard(),
                    config.getIdcard().isDetectDirection(),
                    cardSide,
                    base64
            ).execute();
            Map<String, Object> body = response.body();
            if (CollectionUtils.isEmpty(body) || !body.containsKey("words_result")) {
                throw new HttpRequestException("调用百度身份证OCR错误，response body --> " + JSONUtil.toJSONString(body));
            }
            Map<String, Object> result = (Map<String, Object>) body.get("words_result");
            if (CollectionUtils.isEmpty(result)) {
                throw new HttpRequestException("调用百度身份证OCR错误，response body --> " + JSONUtil.toJSONString(body));
            }

            Idcard idcard = new Idcard();
            result.forEach((key, value) -> {
                if (BaiduOCRFieldPair.IDCARD_PAIRS.containsKey(key) && CententUtil.initialized(value)) {
                    CardEntity entity = JSONUtil.map2Object((Map<String, Object>) value, CardEntity.class);
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
    @SuppressWarnings("unchecked")
    public VehicleLicence vehicleLicence0(String base64, Direction direction) {
        String accessToken = this.getToken();
        String cardSide = direction == Direction.FRONT ? "front" : "back";

        try {
            Response<Map<String, Object>> response = api.vehicleLicence(
                    accessToken,
                    config.getVehicleLicence().isDetectDirection(),
                    config.getVehicleLicence().isUnified(),
                    config.getVehicleLicence().isQualityWarn(),
                    config.getVehicleLicence().isRiskWarn(),
                    cardSide,
                    base64,
                    null
            ).execute();
            Map<String, Object> body = response.body();
            if (CollectionUtils.isEmpty(body) || !body.containsKey("words_result")) {
                throw new HttpRequestException("调用百度行驶证OCR错误，response body --> " + JSONUtil.toJSONString(body));
            }
            Map<String, Object> result = (Map<String, Object>) body.get("words_result");
            if (CollectionUtils.isEmpty(result)) {
                throw new HttpRequestException("调用百度行驶证OCR错误，response body --> " + JSONUtil.toJSONString(body));
            }

            VehicleLicence vehicleLicence = new VehicleLicence();
            result.forEach((key, value) -> {
                if (BaiduOCRFieldPair.VEHICLE_LICENCE_PAIRS.containsKey(key) && CententUtil.initialized(value)) {
                    CardEntity entity = JSONUtil.map2Object((Map<String, Object>) value, CardEntity.class);
                    assert entity != null;
                    BaiduOCRFieldPair.VEHICLE_LICENCE_PAIRS.get(key).accept(vehicleLicence, entity.getWords());
                }
            });

            return vehicleLicence;
        } catch (IOException e) {
            throw new HttpRequestException("调用百度OCR错误，行驶证识别失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public VehicleCertificate vehicleCertificate0(String base64) {
        String accessToken = this.getToken();

        try {
            Response<Map<String, Object>> response = api.vehicleCertificate(accessToken, base64, null)
                    .execute();
            Map<String, Object> body = response.body();
            if (CollectionUtils.isEmpty(body) || !body.containsKey("words_result")) {
                throw new HttpRequestException("调用百度合格证OCR错误，response body --> " + JSONUtil.toJSONString(body));
            }
            Map<String, Object> result = (Map<String, Object>) body.get("words_result");
            if (CollectionUtils.isEmpty(result)) {
                throw new HttpRequestException("调用百度合格证OCR错误，response body --> " + JSONUtil.toJSONString(body));
            }

            VehicleCertificate vehicleCertificate = new VehicleCertificate();
            result.forEach((key, value) -> {
                if (BaiduOCRFieldPair.VEHICLE_CERTIFICATE_PAIRS.containsKey(key) && CententUtil.initialized(value)) {
                    BaiduOCRFieldPair.VEHICLE_CERTIFICATE_PAIRS.get(key).accept(vehicleCertificate, String.valueOf(value));
                }
            });

            return vehicleCertificate;
        } catch (IOException e) {
            throw new HttpRequestException("调用百度OCR错误，行驶证识别失败", e);
        }
    }

    private String getToken() {
        return cache.get(BAIDU_OCR_TOKEN_CACHE_KEY, 29, TimeUnit.DAYS, () -> {
            log.info("百度OCR access_token 无效或过期，重新获取");
            Response<Map<String, String>> response = api.getToken(BaiduOCRAPI.GRANT_TYPE, config.getApiKey(), config.getSecretKey())
                    .execute();
            Map<String, String> body = response.body();
            if (CollectionUtils.isEmpty(body) || !body.containsKey("access_token")) {
                throw new HttpRequestException("调用百度OCR错误，获取access_token失败，response body: " + JSONUtil.toJSONString(body));
            }
            return body.get("access_token");
        });
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
