package com.centent.channel.wechat.official.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.centent.channel.wechat.official.enums.UserRule;
import com.centent.channel.wechat.official.enums.UserStatus;
import com.centent.core.define.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 微信公众号用户信息表
 *
 * @since 0.0.1
 */
@Data
@TableName("wechat_official_user")
@EqualsAndHashCode(callSuper = true)
public class WechatOfficialUser extends BaseEntity {

    /**
     * 微信公众号用户唯一标识
     *
     * @since 0.0.1
     */
    private String openid;

    /**
     * 微信用户在开放平台的唯一标识
     *
     * @since 0.0.1
     */
    private String unionid;

    /**
     * 微信用户昵称
     *
     * @since 0.0.1
     */
    private String nickname;

    /**
     * 用户角色
     *
     * @since 0.0.1
     */
    private UserRule rule;

    /**
     * 用户状态
     *
     * @since 0.0.1
     */
    private UserStatus status;

    public boolean isAvailable() {
        return status == UserStatus.SUBSCRIBE;
    }
}
