package com.centent.ocr.baidu.retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.Map;

public interface BaiduOCRAPI {

    String GRANT_TYPE = "client_credentials";

    @POST("/oauth/2.0/token")
    Call<Map<String, String>> getToken(@Query("grant_type") String grantType, // 固定值：client_credentials
                                       @Query("client_id") String apiKey, // 应用API key
                                       @Query("client_secret") String secretKey); // 应用Secret key

    @FormUrlEncoded
    @POST("/rest/2.0/ocr/v1/idcard")
    Call<Map<String, Object>> idcard(@Query("access_token") String access_token,
                                     @Field("detect_risk") Boolean detectRisk, // 是否开启身份证风险类型(身份证复印件、临时身份证、身份证翻拍、修改过的身份证)检测功能，默认不开启，即：false。
                                     @Field("detect_quality") Boolean detectQuality,
                                     @Field("detect_photo") Boolean detectPhoto,
                                     @Field("detect_card") Boolean detectCard,
                                     @Field("detect_direction") Boolean detectDirection,
                                     @Field("id_card_side") String idCardSide, // front or back
                                     @Field("image") String image); // image和url二选一，优先使用image

    @FormUrlEncoded
    @POST("/rest/2.0/ocr/v1/vehicle_license")
    Call<Map<String, Object>> vehicleLicence(@Query("access_token") String access_token,
                                             @Field("detect_direction") Boolean detectDirection,
                                             @Field("unified") Boolean unified, // 是否开启身份证风险类型(身份证复印件、临时身份证、身份证翻拍、修改过的身份证)检测功能，默认不开启，即：false。
                                             @Field("quality_warn") Boolean qualityWarn,
                                             @Field("risk_warn") Boolean riskWarn,
                                             @Field("vehicle_license_side") String licenseSide, // front or back
                                             @Field("image") String image, // image和url二选一，优先使用image
                                             @Field("url") String url);
}
