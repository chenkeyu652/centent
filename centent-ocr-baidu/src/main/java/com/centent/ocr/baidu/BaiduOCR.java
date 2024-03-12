package com.centent.ocr.baidu;

import com.centent.core.exception.BusinessException;
import com.centent.core.util.FileUtil;
import com.centent.core.util.JSONUtil;
import com.centent.ocr.IOCR;
import com.centent.ocr.baidu.config.BaiduOCRConfig;
import com.centent.ocr.baidu.retrofit.BaiduOCRAPI;
import com.centent.ocr.enums.CardDirection;
import jakarta.annotation.Resource;
import okhttp3.ResponseBody;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.Response;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class BaiduOCR implements IOCR {

    @Resource
    private BaiduOCRAPI baiduOCRAPI;

    @Resource
    private BaiduOCRConfig config;

    @Override
    public String idcard(CardDirection direction, MultipartFile image) {
        if (image.isEmpty()) {
            throw new BusinessException("文件为空");
        }
        // 判断文件是否是图片
        if (Objects.isNull(image.getContentType()) || !image.getContentType().startsWith("image")) {
            throw new BusinessException("请上传图片");
        }
        // image转base64
        String base64 = FileUtil.getFileAsBase64(image, true);
        String accessToken = this.getToken();
        String idCardSide = direction == CardDirection.FRONT ? "front" : "back";
        try {
            Response<ResponseBody> response = baiduOCRAPI.idcard(accessToken,
                            idCardSide,
                            base64,
                            null,
                            config.isDetectRisk(),
                            config.isDetectQuality(),
                            config.isDetectPhoto(),
                            config.isDetectCard(),
                            config.isDetectDirection())
                    .execute();
        } catch (Exception e) {
            throw new BusinessException("调用百度OCR错误，身份证识别失败", e);
        }
        return null;
    }

    @Override
    public String idcard(CardDirection direction, File image) {
        return null;
    }

    @Override
    public String idcard(CardDirection direction, String filePath) {
        String accessToken = this.getToken();
        String base64 = FileUtil.getFileAsBase64(filePath, true);
        String idCardSide = direction == CardDirection.FRONT ? "front" : "back";
        try {
            Response<ResponseBody> response = baiduOCRAPI.idcard(accessToken,
                            idCardSide,
                            base64,
                            null,
                            config.isDetectRisk(),
                            config.isDetectQuality(),
                            config.isDetectPhoto(),
                            config.isDetectCard(),
                            config.isDetectDirection())
                    .execute();
        } catch (Exception e) {
            throw new BusinessException("调用百度OCR错误，身份证识别失败", e);
        }
        return null;
    }

    private String getToken() {
        try {
            Response<ResponseBody> response = baiduOCRAPI.getToken(BaiduOCRAPI.GRANT_TYPE, config.getApiKey(), config.getSecretKey())
                    .execute();
            if (!response.isSuccessful()) {
                throw new BusinessException("调用百度OCR错误，获取access_token失败，response code = " + response.code());
            }
            try (ResponseBody body = response.body()) {
                if (body != null) {
                    String bodyString = body.string();
                    Map<String, Object> result = JSONUtil.json2Map(bodyString);
                    if (CollectionUtils.isEmpty(result) || !result.containsKey("access_token")) {
                        throw new BusinessException("调用百度OCR错误，获取access_token失败，response body: " + bodyString);
                    }
                    return result.get("access_token").toString();
                }
                throw new BusinessException("调用百度OCR错误，获取access_token失败，response body is null");
            }
        } catch (Exception e) {
            throw new BusinessException("调用百度OCR错误，获取access_token失败", e);
        }
    }
}
