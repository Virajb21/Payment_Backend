package com.example.payment.service;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.utils.HmacUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.time.Instant;

@Service
public class PaymentService {

    private final HmacUtil hmacUtil;
    private final IdempotenceyService idempotenceyService;
    public PaymentService(HmacUtil hmacUtil, IdempotenceyService idempotenceyService) {
        this.hmacUtil = hmacUtil;
        this.idempotenceyService = idempotenceyService;


    }

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Value("${payment.hmac.secret}")
    private String secretKey;

    @Value("${payment.hmac.validity-seconds:300}")
    private long allowedSkewSeconds;

    public void processPayment(PaymentRequest request, String receivedSignature) {

        // 🔹 Validate timestamp — prevents replay attack
        if (request.getTimestamp() != null && !request.getTimestamp().isBlank()) {
            try {
                Instant requestTime = Instant.parse(request.getTimestamp());
                Instant now = Instant.now();
                Duration skew = Duration.between(requestTime, now).abs();
                if (skew.getSeconds() > allowedSkewSeconds) {
                    throw new RuntimeException("Request timestamp is outside the allowed time window (" + skew.getSeconds() + "s)");
                }
            } catch (Exception e) {
                throw new RuntimeException("Invalid timestamp format", e);
            }
        }

        // 🔥 Generate expected signature from sorted JSON (NO local ObjectMapper)
        String expectedSignature = hmacUtil.calculateHMAC(request, secretKey);

        // Logging
        log.info("🔹 Normalized JSON used for HMAC: {}", hmacUtil.getNormalizedJson(request));
        log.info("🔹 Expected signature: {}", expectedSignature);
        log.info("🔹 Received signature: {}", receivedSignature);

        // 🔐 Constant-time signature match
        if (!hmacUtil.equalsConstTime(expectedSignature, receivedSignature)) {
            throw new RuntimeException("Signature Mismatch");
        }

        log.info("HMAC Validation SUCCESS for order {} transaction {}", request.getOrderId(), request.getTransactionId());

        boolean firstTime = idempotenceyService.acquire(request.getTransactionId());
        if(!firstTime) {
            log.info("Duplicate transaction detected: {}", request.getTransactionId());
            return;
        }

        // TODO next steps
        // save to DB
        // push into Kafka
        // trigger downstream

        log.info("Transaction accepted for processing: {}",
                request.getTransactionId());
    }
}
