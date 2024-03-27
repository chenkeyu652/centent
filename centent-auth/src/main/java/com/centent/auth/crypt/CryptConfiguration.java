package com.centent.auth.crypt;

import com.centent.core.bean.RSAKeyPair;
import com.centent.core.util.RSAUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class CryptConfiguration {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public RSAKeyPair crypt() {
        return RSAUtil.generateRSAKeyPair();
    }
}
