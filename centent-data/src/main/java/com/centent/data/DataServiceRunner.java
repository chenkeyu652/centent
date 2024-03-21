package com.centent.data;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class DataServiceRunner implements ApplicationRunner {

    @Resource
    private List<DataService> dataServices;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        for (DataService dataService : dataServices) {
            long start = System.currentTimeMillis();
            log.info("[{}]预加载数据中", dataService.name());
            dataService.load();
            log.info("[{}]预加载数据完成，耗时{}ms", dataService.name(), System.currentTimeMillis() - start);
        }
    }
}
