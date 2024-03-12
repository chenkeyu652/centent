package com.centent.ocr.baidu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "centent.ocr.baidu")
public class BaiduOCRConfig {

    /**
     * 百度OCR地址
     *
     * @since 0.0.1
     */
    private String url;

    /**
     * 应用的API Key
     *
     * @since 0.0.1
     */
    private String apiKey;

    /**
     * 应用的Secret Key
     *
     * @since 0.0.1
     */
    private String secretKey;

    /**
     * 是否开启身份证风险类型(身份证复印件、临时身份证、身份证翻拍、修改过的身份证)检测功能，默认不开启，即：false。开启后，请查看返回参数risk_type
     *
     * @since 0.0.1
     */
    private boolean detectRisk = false;

    /**
     * 是否开启身份证质量类型(边框/四角不完整、头像或关键字段被遮挡/马赛克)检测功能，默认不开启，即：false。开启后，请查看返回参数card_quality
     *
     * @since 0.0.1
     */
    private boolean detectQuality = false;

    /**
     * 是否检测头像内容，默认不检测。可选值：true-检测头像并返回头像的 base64 编码及位置信息
     *
     * @since 0.0.1
     */
    private boolean detectPhoto = false;

    /**
     * 是否检测身份证进行裁剪，默认不检测。可选值：true-检测身份证并返回证照的 base64 编码及位置信息
     *
     * @since 0.0.1
     */
    private boolean detectCard = false;

    /**
     * 是否开启图像方向自动矫正功能，默认不开启，可对旋转 90/180/270 度的图片进行自动矫正并识别
     *
     * @since 0.0.1
     */
    private boolean detectDirection = false;
}
