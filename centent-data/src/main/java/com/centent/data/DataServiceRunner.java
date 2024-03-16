package com.centent.data;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DataServiceRunner implements ApplicationRunner {

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, DataService> dataServices = applicationContext.getBeansOfType(DataService.class);
        dataServices.values().forEach(dataService -> {
            long start = System.currentTimeMillis();
            log.info("[{}]预加载数据缓存中", dataService.name());
            dataService.loadCache();
            log.info("[{}]预加载数据缓存完成，耗时{}ms", dataService.name(), System.currentTimeMillis() - start);
        });
    }
}
