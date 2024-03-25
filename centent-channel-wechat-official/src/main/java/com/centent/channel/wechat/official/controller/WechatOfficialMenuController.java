package com.centent.channel.wechat.official.controller;

import com.centent.channel.wechat.official.WechatOfficialChannel;
import com.centent.channel.wechat.official.bean.OfficialMenu;
import com.centent.channel.wechat.official.bean.SNSToken;
import com.centent.channel.wechat.official.service.WechatOfficialService;
import com.centent.core.bean.Result;
import com.centent.core.util.JSONUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/wechat/official/menu")
public class WechatOfficialMenuController {

    @Resource
    private WechatOfficialService wechatOfficialService;

    @Resource
    private WechatOfficialChannel wechatOfficialChannel;

    /**
     * 网页授权回调接口，根据state打开指定的菜单地址（用户打开授权网址的回调接口）
     *
     * @param code  code作为换取网页授权的access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     * @param state 重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节，此处为处理请求URL的spring bean name
     * @see <a href="https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html">微信网页开发-网页授权</a>
     */
    @GetMapping("/redirect")
    public void snsRedirect(@RequestParam("code") String code,
                            @RequestParam("state") String state,
                            HttpServletResponse response) throws IOException {
        SNSToken snsToken = wechatOfficialChannel.getSNSToken(code);
        String viewUrl = wechatOfficialService.getViewUrl(state, JSONUtil.object2Map(snsToken), false);
        response.sendRedirect(viewUrl);
    }

    /**
     * 刷新菜单
     *
     * @return nothing if success
     * @since 0.0.1
     */
    @GetMapping("refresh")
    public Result<Void> refresh() {
        // 刷新本地缓存
        OfficialMenu menu = wechatOfficialService.refreshMenus();

        // 调用微信接口，刷新菜单
        wechatOfficialChannel.createMenu(menu);
        return Result.success();
    }
}
