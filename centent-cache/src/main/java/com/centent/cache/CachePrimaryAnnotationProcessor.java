package com.centent.cache;

import com.centent.core.util.CententUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 缓存实现控制
 * 1. 依据配置控制
 * 2. 配置不存在时，根据@Order注解指定默认实现
 */
@Slf4j
@Component
public class CachePrimaryAnnotationProcessor implements BeanDefinitionRegistryPostProcessor {

    @Value("${centent.cache.primary-service:null}")
    private String primaryService;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        long start = System.currentTimeMillis();
        Map<BeanDefinition, Integer> otherBeanDefinitions = new HashMap<>();
        AtomicReference<BeanDefinition> primaryBeanDefinition = new AtomicReference<>();

        Arrays.stream(registry.getBeanDefinitionNames()).forEach(beanName -> {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);

            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null && beanClassName.startsWith("com.centent.cache")) {
                Class<?> resolved = ClassUtils.resolveClassName(beanClassName, ClassUtils.getDefaultClassLoader());
                if (ICache.class.isAssignableFrom(resolved)) {
                    Order order = resolved.getAnnotation(Order.class);
                    int orderValue = order != null ? order.value() : Ordered.LOWEST_PRECEDENCE;
                    if (Objects.equals(primaryService, beanClassName)) {
                        primaryBeanDefinition.set(beanDefinition);
                    } else {
                        otherBeanDefinitions.put(beanDefinition, orderValue);
                    }
                }
            }
        });
        if (CententUtil.initialized(primaryService) && Objects.isNull(primaryBeanDefinition.get())) {
            throw new IllegalArgumentException("未找到缓存实现：" + primaryService);
        }

        // 配置了默认缓存类，则使用该类作为默认缓存实现
        if (Objects.nonNull(primaryBeanDefinition.get())) {
            log.info("加载默认缓存实现：{}", primaryBeanDefinition.get().getBeanClassName());
            primaryBeanDefinition.get().setPrimary(true);
            otherBeanDefinitions.forEach((definition, orderValue) -> {
                definition.setPrimary(false);
                log.info("加载其它缓存实现：{}", definition.getBeanClassName());
            });
        }
        // 没有配置默认缓存类，则将实现的缓存类按照@Order顺序排序，第一个为默认缓存实现
        else if (!otherBeanDefinitions.isEmpty()) {
            LinkedHashMap<BeanDefinition, Integer> sortedDefinitions = otherBeanDefinitions.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .filter(entry -> {
                        // 通过@Order注解排序后，将order最小的缓存实现拿出来作为主要缓存实现
                        if (Objects.isNull(primaryBeanDefinition.get())) {
                            log.info("加载默认缓存实现(@Order={})：{}", entry.getValue(), entry.getKey().getBeanClassName());
                            entry.getKey().setPrimary(true);
                            primaryBeanDefinition.set(entry.getKey());
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            sortedDefinitions.forEach((definition, orderValue) -> {
                definition.setPrimary(false);
                log.info("加载其它缓存实现(@Order={})：{}", orderValue, definition.getBeanClassName());
            });
        } else {
            log.warn("未找到任何缓存实现");
        }
        log.info("加载缓存实现完成，耗时：{}ms", System.currentTimeMillis() - start);
    }
}
