package com.centent.auth.sign;

import com.centent.core.util.SignatureUtil;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@Order(-1)
public class SignAspect {

    private static final Map<Class<?>, SignHandler> SIGN_HANDLERS = new ConcurrentHashMap<>();

    @Resource
    private SignConfig signConfig;

    @Resource
    private HttpServletRequest request;

    @Before("@within(com.centent.auth.sign.Sign) || @annotation(com.centent.auth.sign.Sign)")
    public void beforeClass(JoinPoint joinPoint) {
        Sign sign = this.getAnnotation(joinPoint, Sign.class);
        Assert.notNull(sign, "@Sign注解AOP不可能为空");

        String appId = this.getHeader("X-Auth-App");
        String time = this.getHeader("X-Auth-Time");
        String signStr = this.getHeader("X-Auth-Sign");

        // TODO...time有效期校验，前后不能超过5分钟

        List<String> signKeys = Lists.newArrayList(time, appId); // 基础验签，根据时间+随机数进行验签，优先级最低
        // 通过注解指定的SignHandler实现类进行验签
        if (!sign.value().equals(NoneSignHandler.class) && SignHandler.class.isAssignableFrom(sign.value())) {
            SignHandler handler = SIGN_HANDLERS.computeIfAbsent(sign.value(), (key) -> {
                try {
                    return (SignHandler) sign.value().getConstructor().newInstance();
                } catch (Exception e) {
                    log.error("注解指定的实现类实例化失败：" + sign.value().getName(), e);
                    throw new RuntimeException("签名验证失败", e);
                }
            });
            signKeys.addAll(handler.getSignKeys(joinPoint.getArgs()));
        } else {
            // 从方法入参中寻找SignKey的实现
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (Objects.nonNull(arg) && arg instanceof SignKey) {
                    signKeys.addAll(((SignKey) arg).signKeys());
                }
            }
        }
        signKeys = signKeys.stream().filter(Strings::isNotEmpty).collect(Collectors.toList());
        // 按照字典顺序排序
        signKeys.sort(String::compareTo);
        String message = String.join("", signKeys);
        log.info("sign --> target: {}, appId: {}, message: {}", joinPoint.getSignature().toShortString(), appId, message);
        String checkSign = SignatureUtil.signSHA256(signConfig.getPairs().get(appId), message, true);
        if (!Objects.equals(checkSign, signStr)) {
            String errorMsg = MessageFormat.format("验签失败，signStr:{0}, checkSign:{1}, appId:{2}, message:{3}",
                    signStr, checkSign, appId, message);
            log.error(errorMsg);
            throw new RuntimeException("签名验证错误");
        }
    }

    private <T extends Annotation> T getAnnotation(JoinPoint joinPoint, Class<T> clazz) {
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature ms)) {
            return null;
        }
        Method method = ms.getMethod();
        T annotation = method.getAnnotation(clazz);
        if (Objects.nonNull(annotation)) {
            return annotation;
        }
        Class<?> targetClass = joinPoint.getTarget().getClass();
        if (targetClass.isAnnotationPresent(clazz)) {
            return targetClass.getAnnotation(clazz);
        }
        return null;
    }

    private String getHeader(String name) {
        String header = request.getHeader(name);
        if (Objects.isNull(header)) {
            log.error("签名验证错误，请求头[{}]不存在", name);
            throw new RuntimeException("签名验证错误");
        }
        return header;
    }

    static class NoneSignHandler implements SignHandler {
        @Override
        public List<String> getSignKeys(Object[] args) {
            return null;
        }
    }
}
