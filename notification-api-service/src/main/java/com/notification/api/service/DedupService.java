package com.notification.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class DedupService {

    private final StringRedisTemplate redis;

    public boolean isDuplicate(Long userId, String message) {

        String key = "dedup:" + userId + ":" + message.hashCode();

        Boolean exists = redis.hasKey(key);

        if (Boolean.TRUE.equals(exists)) {
            return true;
        }

        redis.opsForValue().set(key, "1", Duration.ofMinutes(5));
        return false;
    }
}
