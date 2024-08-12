package com.example.stylish.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY = "rate_limiter";
    private static final int LIMIT = 10;
    private static final long TTL = 1;

    @Autowired
    public RateLimiterService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String ip) {
        // get count
        Integer count = (Integer) redisTemplate.opsForHash().get(KEY, ip);
        // if IP is not in hash, set count=1 & set TTL
        if (count == null) {
            redisTemplate.opsForHash().put(KEY, ip, 1);
            redisTemplate.expire(KEY, TTL, TimeUnit.SECONDS);
            return true;
        } else {
            if (count >= LIMIT) {
                return false;
            } else {
                redisTemplate.opsForHash().increment(KEY, ip, 1);
                return true;
            }
        }
    }
}




