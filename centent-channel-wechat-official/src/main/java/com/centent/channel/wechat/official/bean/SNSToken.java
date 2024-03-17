package com.centent.channel.wechat.official.bean;

import lombok.Data;

/**
 * 微信公众号网页授权信息
 *
 * @since 0.0.1
 */
@Data
public class SNSToken {

    private String accessToken;

    private Long expiresIn;

    private String refreshToken;

    private String openid;

    private String scope;

    private String isSnapshotuser;

    private String unionid;
}
