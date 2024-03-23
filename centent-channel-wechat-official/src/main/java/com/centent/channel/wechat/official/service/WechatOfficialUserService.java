package com.centent.channel.wechat.official.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.centent.channel.wechat.official.entity.WechatOfficialUser;
import com.centent.channel.wechat.official.mapper.WechatOfficialUserMapper;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Service
public class WechatOfficialUserService {

    @Resource
    private WechatOfficialUserMapper mapper;

    public void insert(WechatOfficialUser user) {
        mapper.insert(user);
    }

    public void update(WechatOfficialUser user) {
        mapper.updateById(user);
    }

    public WechatOfficialUser getIfExistByOpenid(String openid) {
        if (Strings.isBlank(openid)) {
            throw new IllegalArgumentException("openid不能为空");
        }
        LambdaQueryWrapper<WechatOfficialUser> wrapper = new LambdaQueryWrapper<WechatOfficialUser>()
                .eq(WechatOfficialUser::getOpenid, openid);
        return mapper.selectOne(wrapper);
    }

    public WechatOfficialUser selectByOpenid(String openid) {
        if (Strings.isBlank(openid)) {
            throw new IllegalArgumentException("openid不能为空");
        }
        LambdaQueryWrapper<WechatOfficialUser> wrapper = new LambdaQueryWrapper<WechatOfficialUser>()
                .eq(WechatOfficialUser::getOpenid, openid);
        WechatOfficialUser user = mapper.selectOne(wrapper);
        if (user == null) {
            throw new IllegalArgumentException("通过openid找不到微信公众号用户，openid: " + openid);
        }
        return user;
    }

    public WechatOfficialUser selectByUnionid(String unionid) {
        if (Strings.isBlank(unionid)) {
            throw new IllegalArgumentException("unionid不能为空");
        }
        LambdaQueryWrapper<WechatOfficialUser> wrapper = new LambdaQueryWrapper<WechatOfficialUser>()
                .eq(WechatOfficialUser::getUnionid, unionid);
        WechatOfficialUser user = mapper.selectOne(wrapper);
        if (user == null) {
            throw new IllegalArgumentException("通过unionid找不到微信公众号用户，unionid: " + unionid);
        }
        return user;
    }
}
