package com.centent.storage.local.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "centent.storage.local")
public class StorageLocalConfig {

    /**
     * 本地文件存储根目录
     *
     * @since 0.0.1
     */
    private String rootPath;
}
