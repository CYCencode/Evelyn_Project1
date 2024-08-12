package com.example.stylish.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacebookConfig {

    @Value("${facebook.app.id}")
    private String appId;

    @Value("${facebook.app.secret}")
    private String appSecret;

    @Bean
    public String getFacebookAppId() {
        return appId;
    }

    @Bean
    public String getFacebookAppSecret() {
        return appSecret;
    }
}
