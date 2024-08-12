package com.example.stylish.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String externalResourcePath = "file:" + System.getProperty("user.home") + "/uploads/img/";
        registry.addResourceHandler("/img/**").addResourceLocations(externalResourcePath);
    }
}
