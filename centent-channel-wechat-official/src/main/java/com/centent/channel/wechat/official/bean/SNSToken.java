package com.centent.channel.wechat.official.bean;

import lombok.Data;

/**
 * 微信公众号网页授权信息
 *
 * @since 0.0.1
 */
@Data
public class SNSToken {

    private String access_token;

    private Long expires_in;

    private String refresh_token;

    private String openid;

    private String scope;

    private String is_snapshotuser;

    private String unionid;
}
