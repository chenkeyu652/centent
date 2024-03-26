package com.centent.auth.sign;

import com.centent.core.util.SignatureUtil;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

@Slf4j
@Aspect
@Component
@Order(-1)
public class SignAspect {

    @Value("${centent.auth.sign.secret:92423224-71c2-4e1b-a239-a0d457a6b9bf}")
    private String secret;

    @Resource
    private HttpServletRequest request;

    @Before("@within(com.centent.auth.sign.Sign) || @annotation(com.centent.auth.sign.Sign)")
    public void beforeClass(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        if (signature instanceof MethodSignature ms) {
            Method method = ms.getMethod();
            Sign sign = method.getAnnotation(Sign.class);
            Class<?> targetClass = joinPoint.getTarget().getClass();
            if (Objects.isNull(sign) && targetClass.isAnnotationPresent(Sign.class)) {
                sign = targetClass.getAnnotation(Sign.class);
            }
            if (Objects.isNull(sign)) {
                return;
            }

            String appId = this.getHeader("X-Auth-appId");
            String time = this.getHeader("X-Auth-Time");
            String signStr = this.getHeader("X-Auth-Sign");

            // TODO...time有效期校验，前后不能超过5分钟

            List<String> signKeys = Lists.newArrayList(time, appId); // 基础验签，根据时间+随机数进行验签，优先级最低
            // 通过注解指定的SignExecutor实现类进行验签
            if (!sign.value().equals(Void.class) && SignHandler.class.isAssignableFrom(sign.value())) {
                try {
                    SignHandler executor = (SignHandler) sign.value().getConstructor().newInstance();
                    signKeys = executor.getSignKeys(appId, time, joinPoint.getArgs());
                } catch (Exception e) {
                    log.error("注解指定的SignExecutor实现类异常", e);
                }
            } else {
                // 从方法入参中找SignBO实现，通过找到的第一个SignBO参数进行验签
                Object[] args = joinPoint.getArgs();
                for (Object arg : args) {
                    if (arg instanceof SignBO) {
                        signKeys = ((SignBO) arg).getSignKeys(appId, time);
                        break;
                    }
                }
            }
            // 按照字典顺序排序后验签
            signKeys.sort(String::compareTo);
            String message = String.join("", signKeys);
            String checkSign = SignatureUtil.signSHA256(secret, message);
            if (!Objects.equals(checkSign, signStr)) {
                String errorMsg = MessageFormat.format("验签失败，appId:{0}, message:{1}, signStr:{2}, checkSign:{3}",
                        appId, message, signStr, checkSign);
                throw new RuntimeException(errorMsg);
            }
        }
    }

    private String getHeader(String name) {
        String header = request.getHeader(name);
        if (Objects.isNull(header)) {
            throw new RuntimeException("header[" + name + "] is null");
        }
        return header;
    }
}
