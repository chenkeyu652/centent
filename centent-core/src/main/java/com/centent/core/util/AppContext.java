package com.centent.core.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Lazy(false)
public class AppContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    // 取得存储在静态变量中的ApplicationContext
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    // 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        AppContext.applicationContext = applicationContext;
    }

    // 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    // 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型
    // 如果有多个Bean符合Class, 取出第一个
    public static <T> T getBean(Class<T> requiredType) {
        checkApplicationContext();
        return applicationContext.getBean(requiredType);
    }

    // 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型
    // 如果有多个Bean符合Class, 取出第一个
    public static <T> Map<String, T> getBeans(Class<T> requiredType) {
        checkApplicationContext();
        return applicationContext.getBeansOfType(requiredType);
    }

    // 清除applicationContext静态变量
    public static void cleanApplicationContext() {
        applicationContext = null;
    }

    // 检查是否存在
    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder或配置@Lazy(false)");
        }
    }
}
