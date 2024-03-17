package com.centent.channel.wechat.official.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.Map;

public interface WechatOfficialAPI {

    String GRANT_TYPE = "client_credential";

    @GET("/cgi-bin/token")
    Call<Map<String, String>> getToken(@Query("grant_type") String grantType, // 固定值：client_credentials
                                       @Query("appid") String appId, // 应用API key
                                       @Query("secret") String appSecret); // 应用Secret key
}
