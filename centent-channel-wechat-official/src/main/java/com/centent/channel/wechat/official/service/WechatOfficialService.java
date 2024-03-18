package com.centent.channel.wechat.official.service;

import com.centent.channel.wechat.official.bean.OfficialMenu;
import com.centent.channel.wechat.official.config.WechatOfficialConfig;
import com.centent.core.exception.BusinessException;
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

@Slf4j
@Service
public class WechatOfficialService {

    private static final String MENU_FILE_PATH = "wechat-official-menu.json";

    // 网页类型菜单缓存
    private static final Map<String, OfficialMenu.Button> VIEW_MENUS = new HashMap<>();

    @Resource
    private WechatOfficialConfig config;

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
                this.handleButton(topButton);
            }
            // 二级菜单
            else {
                if (subButtons.size() > 5) {
                    throw new BusinessException("二级菜单数组，个数应为1~5个");
                }
                for (OfficialMenu.Button subButton : subButtons) {
                    this.handleButton(subButton);
                }
            }
        }
        log.info("加载微信公众号菜单配置完成...");
        return menu;
    }

    public String getViewUrl(String state) {
        if (CententUtil.uninitialized(state) || !VIEW_MENUS.containsKey(state)) {
            return null;
        }
        return VIEW_MENUS.get(state).getOriginalUrl();
    }

    private void handleButton(OfficialMenu.Button button) {
        if (button.isValidView()) {
            if (VIEW_MENUS.containsKey(button.getKey())) {
                OfficialMenu.Button repeat = VIEW_MENUS.get(button.getKey());
                throw new BusinessException("菜单key重复：key=" + button.getKey() + "，name1：" + button.getName() + "，name2：" + repeat.getName());
            }

            button.setOriginalUrl(button.getUrl());
            String url = config.getAuthDomain() + "/wechat/official/menu/redirect";
            url = URLEncoder.encode(url, StandardCharsets.UTF_8);
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?"
                    + "appid=" + config.getAppId() + "&redirect_uri=" + url
                    + "&response_type=code&scope=snsapi_base&state=" + button.getKey() + "#wechat_redirect";
            button.setUrl(URLEncoder.encode(url, StandardCharsets.UTF_8));
            VIEW_MENUS.put(button.getKey(), button);
        }
    }
}
