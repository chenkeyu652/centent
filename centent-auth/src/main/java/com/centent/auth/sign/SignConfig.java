package com.centent.auth.sign;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "centent.auth.sign")
public class SignConfig {

    private Map<String, String> pairs;
}
