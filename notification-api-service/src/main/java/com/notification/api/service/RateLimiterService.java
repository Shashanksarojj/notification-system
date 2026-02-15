package com.notification.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate redis;

    private static final int LIMIT = 5; // per minute

    public boolean isAllowed(Long userId) {

        String key = "rate_limit:" + userId;

        Long count = redis.opsForValue().increment(key);

        if (count == 1) {
            redis.expire(key, Duration.ofMinutes(1));
        }

        return count <= LIMIT;
    }
}