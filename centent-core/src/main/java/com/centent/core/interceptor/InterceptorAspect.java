package com.centent.core.interceptor;

import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.centent.core.define.Interceptor;
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

import java.lang.reflect.Method;
import java.util.Map;

@Aspect
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
public class InterceptorAspect {

    @Resource
    private ApplicationContext applicationContext;


    @Pointcut("@annotation(com.centent.core.interceptor.Interceptor)")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object doAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<? extends Interceptor<?, ?>> clazz = this.getInterceptor(joinPoint);
        Map<String, ? extends Interceptor<?, ?>> interceptors = applicationContext.getBeansOfType(clazz);
        // 获取第一个参数
        Object[] args = joinPoint.getArgs();
        final Object param = ArrayUtils.isNotEmpty(args) ? args[0] : null;
        for (Interceptor<?, ?> interceptor : interceptors.values()) {
            interceptor.before0(param);
        }

        Object proceed = joinPoint.proceed();

        for (Interceptor<?, ?> interceptor : interceptors.values()) {
            interceptor.after0(param, proceed);
        }
        return proceed;
    }

    private Class<? extends Interceptor<?, ?>> getInterceptor(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        com.centent.core.interceptor.Interceptor annotation =
                method.getAnnotation(com.centent.core.interceptor.Interceptor.class);
        return annotation.value();
    }
}
