package com.centent.core.listener;

import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.centent.core.define.IListener;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
public class ListenerAspect {

    private static final Map<Class<? extends IListener<Object, Object>>, Collection<? extends IListener<Object, Object>>>
            LISTENER_MAP = new ConcurrentHashMap<>();

    @Resource
    private ApplicationContext applicationContext;


    @Pointcut("@annotation(com.centent.core.listener.Listener)")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object doAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<? extends IListener<Object, Object>> clazz = this.getListeners(joinPoint);
        Collection<? extends IListener<Object, Object>> listeners = LISTENER_MAP.computeIfAbsent(clazz, k -> {
            Map<String, ? extends IListener<Object, Object>> beans = applicationContext.getBeansOfType(clazz);
            return CollectionUtils.isEmpty(beans) ? Collections.emptyList() : beans.values();
        });

        // 获取第一个参数
        Object[] args = joinPoint.getArgs();
        final Object param = ArrayUtils.isNotEmpty(args) ? args[0] : null;

        // TODO...有些任务可以异步执行
        listeners.stream()
                .filter(i -> i.preCheck(param))
                .forEach(i -> i.preHandle(param));

        Object proceed = joinPoint.proceed();

        listeners.stream()
                .filter(i -> i.postCheck(param, proceed))
                .forEach(i -> i.postHandle(param, proceed));

        return proceed;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends IListener<Object, Object>> getListeners(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        com.centent.core.listener.Listener annotation =
                method.getAnnotation(com.centent.core.listener.Listener.class);
        return (Class<? extends IListener<Object, Object>>) annotation.value();
    }
}
