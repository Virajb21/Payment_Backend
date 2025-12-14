package com.example.payment.service;
import com.example.payment.utils.HmacUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service


public class IdempotenceyService {

    private final StringRedisTemplate redisTemplate;

    public IdempotenceyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final Duration TTL = Duration.ofDays(7);

   public boolean acquire(String transactionId) {
        String key = "idempotency:" + transactionId;

        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "PROCESSED", TTL);
        return Boolean.TRUE.equals(success);

   }
}
