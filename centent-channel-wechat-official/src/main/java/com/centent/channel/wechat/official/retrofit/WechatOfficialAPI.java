package com.centent.channel.wechat.official.retrofit;

import com.centent.channel.wechat.official.bean.OfficialMenu;
import com.centent.channel.wechat.official.bean.SNSToken;
import com.centent.channel.wechat.official.bean.TemplateMessage;
import lombok.Data;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.Map;

public interface WechatOfficialAPI {

    String API_GRANT_TYPE = "client_credential";

    String SNS_GRANT_TYPE = "authorization_code";

    /**
     * 接口数据请求token
     *
     * @param grantType 固定值：client_credential
     * @param appId     应用API key
     * @param appSecret 应用Secret key
     * @return 响应数据，示例：{"access_token":"这里是ACCESS_TOKEN","expires_in":7200}
     * @since 0.0.1
     */
    @GET("/cgi-bin/token")
    Call<Map<String, String>> getAPIToken(@Query("grant_type") String grantType,
                                          @Query("appid") String appId,
                                          @Query("secret") String appSecret);

    /**
     * 获取微信用户网页授权Token和用户唯一标识openid
     * NOTE：刷新access_token（如果需要），由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，
     * refresh_token有效期为30天，当refresh_token失效之后，需要用户重新授权。
     *
     * @param grantType 固定值：authorization_code
     * @param appId     应用API key
     * @param appSecret 应用Secret key
     * @param code      用户授权后生成的临时CODE
     * @return 响应数据，示例：{"access_token":"这里是ACCESS_TOKEN","expires_in":7200,"refresh_token":"这里是REFRESH_TOKEN",
     * "openid":"用户唯一标识OPENID","scope":"用户授权的作用域SCOPE"}
     * @since 0.0.1
     */
    @GET("/sns/oauth2/access_token")
    Call<SNSToken> getSNSToken(@Query("grant_type") String grantType,
                               @Query("appid") String appId,
                               @Query("secret") String appSecret,
                               @Query("code") String code);

    /**
     * 通过网页授权Token和用户唯一标识openid获取用户信息
     *
     * @param snsAccessToken 网页授权Token
     * @param openId         用户唯一标识
     * @return 响应数据，示例：{"subscribe":1,"openid":"用户唯一标识","subscribe_time":"13位时间戳","unionid":"用户unionid","tagid_list":[123,2],},
     */
    @GET("/cgi-bin/user/info")
    Call<SNSToken> getUserInfo(@Query("access_token") String snsAccessToken,
                               @Query("openid") String openId);

    /**
     * 创建自定义菜单
     *
     * @param accessToken Token
     * @param menu        参数数据
     * @return 响应数据，示例：{"errcode":0,"errmsg":"ok"}
     * @since 0.0.1
     */
    @POST("/cgi-bin/menu/create")
    Call<APIResult> createMenu(@Query("access_token") String accessToken, @Body OfficialMenu menu);


    /**
     * 发送模板消息
     *
     * @param accessToken Token
     * @param message     参数数据
     * @return 响应数据，示例：{"errcode":0,"errmsg":"ok","msgid":"msgid"}
     * @since 0.0.1
     */
    @POST("/cgi-bin/message/template/send")
    Call<APIResult> sendTemplateMessage(@Query("access_token") String accessToken,
                                        @Body TemplateMessage message);


    @Data
    class APIResult {

        private String errcode;

        private String errmsg;

        private String msgid;
    }
}
