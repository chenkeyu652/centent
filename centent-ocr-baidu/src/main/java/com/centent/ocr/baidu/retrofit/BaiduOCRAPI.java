package com.centent.ocr.baidu.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BaiduOCRAPI {

    String GRANT_TYPE = "client_credentials";

    @FormUrlEncoded
    @POST("/oauth/2.0/token")
    Call<ResponseBody> getToken(@Field("grant_type") String grantType, // 固定值：client_credentials
                                @Field("client_id") String apiKey, // 应用API key
                                @Field("client_secret") String secretKey); // 应用Secret key

    @FormUrlEncoded
    @POST("/rest/2.0/ocr/v1/idcard")
    Call<ResponseBody> idcard(@Query("access_token") String access_token,
                              @Field("id_card_side") String idCardSide, // front or back
                              @Field("image") String image, // image和url二选一，优先使用image
                              @Field("url") String url, // image和url二选一，优先使用image
                              @Field("detect_risk") Boolean detectRisk, // 是否开启身份证风险类型(身份证复印件、临时身份证、身份证翻拍、修改过的身份证)检测功能，默认不开启，即：false。
                              @Field("detect_quality") Boolean detectQuality,
                              @Field("detect_photo") Boolean detectPhoto,
                              @Field("detect_card") Boolean detectCard,
                              @Field("detect_direction") Boolean detectDirection);
}
