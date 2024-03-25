package com.centent.channel.wechat.official.service;

import com.centent.channel.wechat.official.bean.OfficialMenu;
import com.centent.channel.wechat.official.config.WechatOfficialConfig;
import com.centent.channel.wechat.official.entity.WechatOfficialUser;
import com.centent.channel.wechat.official.enums.UserRule;
import com.centent.channel.wechat.official.enums.UserStatus;
import com.centent.core.exception.BusinessException;
import com.centent.core.exception.IllegalArgumentException;
import com.centent.core.util.CententUtil;
import com.centent.core.util.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class WechatOfficialService {

    private static final String MENU_FILE_PATH = "wechat-official-menu.json";

    // 网页类型菜单缓存
    private static final Map<String, OfficialMenu.Button> VIEW_MENUS = new HashMap<>();

    @Resource
    private WechatOfficialConfig config;

    @Resource
    private WechatOfficialUserService userService;

    public OfficialMenu refreshMenus() {
        log.info("加载微信公众号菜单配置中...");
        VIEW_MENUS.clear();
        // 读取resources目录下的wechat-official-menu.json配置文件
        URL resource = this.getClass().getClassLoader().getResource(MENU_FILE_PATH);
        if (resource == null) {
            throw new BusinessException("未找到微信公众号菜单配置文件：" + MENU_FILE_PATH);
        }
        // 读取文件内容
        OfficialMenu menu;
        try (InputStream inputStream = resource.openStream()) {
            String menuJson = new String(inputStream.readAllBytes());
            log.debug("微信公众号菜单配置：\n{}", menuJson);
            menu = JSONUtil.json2Object(menuJson, OfficialMenu.class);
        } catch (Exception e) {
            throw new BusinessException("读取微信公众号菜单配置文件失败：" + MENU_FILE_PATH, e);
        }
        if (menu == null) {
            throw new BusinessException("读取微信公众号菜单配置文件失败，文件内容为空：" + MENU_FILE_PATH);
        }
        List<OfficialMenu.Button> topButtons = menu.getButton();
        if (CollectionUtils.isEmpty(topButtons) || topButtons.size() > 3) {
            throw new BusinessException("一级菜单数组，个数应为1~3个");
        }
        for (OfficialMenu.Button topButton : topButtons) {
            List<OfficialMenu.Button> subButtons = topButton.getSub_button();
            // 普通菜单
            if (CollectionUtils.isEmpty(subButtons)) {
                this.handleMenuButton(topButton);
            }
            // 二级菜单
            else {
                if (subButtons.size() > 5) {
                    throw new BusinessException("二级菜单数组，个数应为1~5个");
                }
                for (OfficialMenu.Button subButton : subButtons) {
                    this.handleMenuButton(subButton);
                }
            }
        }
        log.info("加载微信公众号菜单配置完成...");
        return menu;
    }

    public String getViewUrl(String state, Map<String, Object> params, boolean withDomain) {
        if (CententUtil.uninitialized(state) || !VIEW_MENUS.containsKey(state)) {
            throw new IllegalArgumentException("state参数非法：" + state);
        }
        String originalUrl = VIEW_MENUS.get(state).getOriginalUrl();
        if (CollectionUtils.isEmpty(params)) {
            return originalUrl;
        }
        String requestUrl = originalUrl;
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (CententUtil.uninitialized(value)) {
                value = "";
            }
            requestUrl = requestUrl.replace("{" + key + "}", value.toString());
        }
        if (withDomain) {
            requestUrl = config.getAuthDomain() + requestUrl;
        }
        return requestUrl;
    }

    public String getAuthorizeUrl(String redirectUrl, String state) {
        if (!redirectUrl.startsWith("http")) {
            if (!redirectUrl.startsWith("/")) {
                redirectUrl = "/" + redirectUrl;
            }
            redirectUrl = config.getAuthDomain() + redirectUrl;
        }
        redirectUrl = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8);
        return "https://open.weixin.qq.com/connect/oauth2/authorize?"
                + "appid=" + config.getAppId() + "&redirect_uri=" + redirectUrl
                + "&response_type=code&scope=snsapi_base&state=" + state + "#wechat_redirect";
    }

    public String getAuthorizeUrl(String redirectUrl) {
        return this.getAuthorizeUrl(redirectUrl, "unused");
    }

    private void handleMenuButton(OfficialMenu.Button button) {
        if (button.isValidView()) {
            return;
        }
        if (VIEW_MENUS.containsKey(button.getKey())) {
            OfficialMenu.Button repeat = VIEW_MENUS.get(button.getKey());
            throw new BusinessException("菜单key重复：key=" + button.getKey() +
                    "，name1：" + button.getName() + "，name2：" + repeat.getName());
        }

        button.setOriginalUrl(button.getUrl());
        String url = config.getAuthDomain() + "/wechat/official/menu/redirect";
        button.setUrl(this.getAuthorizeUrl(url, button.getKey()));
        VIEW_MENUS.put(button.getKey(), button);
    }

    /**
     * 用户订阅公众号事件
     *
     * @param params XML解析数据
     * @since 0.0.1
     */
    public void subscribe(Map<String, String> params) {
        String openid = params.get("FromUserName");
        WechatOfficialUser user = userService.getIfExistByOpenid(openid);
        if (Objects.nonNull(user)) {
            if (user.getStatus() == UserStatus.UNSUBSCRIBE
                    || user.getStatus() == UserStatus.FORBIDDEN_UNSUBSCRIBE) {
                user.setStatus(user.getStatus().reverse());
                userService.update(user);
                log.debug("触发微信事件：用户取关后重新订阅公众号，openid={}", openid);
            }
        } else {
            user = new WechatOfficialUser();
            user.setOpenid(openid);
            user.setStatus(UserStatus.SUBSCRIBE);
            user.setRule(UserRule.NORMAL); // 默认是普通用户
            userService.insert(user);
            log.debug("触发微信事件：用户首次订阅公众号，openid={}", openid);
        }
    }

    /**
     * 用户取消订阅公众号事件
     *
     * @param params XML解析数据
     * @since 0.0.1
     */
    public void unsubscribe(Map<String, String> params) {
        String openid = params.get("FromUserName");
        WechatOfficialUser user = userService.getIfExistByOpenid(openid);
        if (Objects.nonNull(user)) {
            if (user.getStatus() == UserStatus.SUBSCRIBE
                    || user.getStatus() == UserStatus.FORBIDDEN_SUBSCRIBE) {
                user.setStatus(user.getStatus().reverse());
                userService.update(user);
                log.debug("触发微信事件：用户取消订阅，openid={}", openid);
            }
        }
    }
}
