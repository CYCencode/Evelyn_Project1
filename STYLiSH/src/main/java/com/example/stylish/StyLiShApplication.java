package com.example.stylish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class StyLiShApplication {
    public static void main(String[] args) {
        SpringApplication.run(StyLiShApplication.class, args);
    }
}
