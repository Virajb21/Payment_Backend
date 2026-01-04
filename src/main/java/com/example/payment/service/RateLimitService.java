package com.example.payment.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    // One bucket per client (IP / key)
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket newBucket() {
        Refill refill = Refill.intervally(100, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(100, refill);
        return Bucket4j.builder().addLimit(limit).build();
    }

    public boolean isAllowed(String clientKey) {
        Bucket bucket = cache.computeIfAbsent(clientKey, k -> newBucket());
        return bucket.tryConsume(1);
    }
}
