package com.centent.ocr.baidu;

import com.centent.core.exception.HttpRequestException;
import com.centent.core.util.CententUtil;
import com.centent.core.util.JSONUtil;
import com.centent.ocr.IOCR;
import com.centent.ocr.baidu.config.BaiduOCRConfig;
import com.centent.ocr.baidu.retrofit.BaiduOCRAPI;
import com.centent.ocr.bean.Idcard;
import com.centent.ocr.enums.Direction;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import retrofit2.Response;

import java.util.Map;
import java.util.function.BiConsumer;

public class BaiduOCR extends IOCR {

    private static final Map<String, BiConsumer<Idcard, String>> PAIRS = Map.of(
            "公民身份号码", Idcard::setNumber,
            "姓名", Idcard::setName,
            "性别", Idcard::setGender,
            "住址", Idcard::setAddress,
            "出生", Idcard::setBirthDay,
            "民族", Idcard::setNation,
            "签发机关", Idcard::setIssuingAuthority,
            "签发日期", Idcard::setIssuingDate,
            "失效日期", Idcard::setExpirationDate
    );

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
                            config.isDetectRisk(),
                            config.isDetectQuality(),
                            config.isDetectPhoto(),
                            config.isDetectCard(),
                            config.isDetectDirection())
                    .execute();
            Map<String, Object> body = response.body();
            if (CollectionUtils.isEmpty(body) || !body.containsKey("words_result")) {
                throw new HttpRequestException("调用百度身份证OCR错误，response body is empty --> " + JSONUtil.toJSONString(body));
            }

            Idcard idcard = new Idcard();
            body.forEach((key, value) -> {
                if (PAIRS.containsKey(key) && CententUtil.initialized(value)) {
                    CardEntity entity = JSONUtil.json2Object(value.toString(), CardEntity.class);
                    assert entity != null;
                    PAIRS.get(key).accept(idcard, entity.getWords());
                }
            });

            return idcard;
        } catch (Exception e) {
            throw new HttpRequestException("调用百度OCR错误，身份证识别失败", e);
        }
    }

    @Override
    public String vehicleLicence(String base64, Direction direction) {
        return null;
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
