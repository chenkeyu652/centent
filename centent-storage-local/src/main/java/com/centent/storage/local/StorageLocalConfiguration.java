package com.centent.storage.local;

import com.centent.storage.IStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "centent.storage.service.impl", havingValue = "com.centent.storage.local.StorageLocal", matchIfMissing = true)
public class StorageLocalConfiguration {

    @Bean
    public IStorage storageLocal() {
        log.info("register conditional bean: storageLocal");
        return new StorageLocal();
    }
}
