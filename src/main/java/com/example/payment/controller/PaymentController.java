package com.example.payment.controller;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.service.PaymentService;
import com.example.payment.service.RateLimitService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final RateLimitService rateLimitService;

    public PaymentController(PaymentService paymentService,
                             RateLimitService rateLimitService) {
        this.paymentService = paymentService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping("/receive")
    public ResponseEntity<String> receivePayment(
            @Valid @RequestBody PaymentRequest paymentData,
            @RequestHeader("X-Signature") String signature,
            HttpServletRequest request) {

        String clientIp = "LOCAL_TEST";
        //request.getRemoteAddr();

        // 🔒 RATE LIMIT CHECK
        if (!rateLimitService.isAllowed(clientIp)) {
            log.warn("Rate limit exceeded for IP {}", clientIp);
            return ResponseEntity
                    .status(429)
                    .body("Too many requests");
        }

        log.debug("Received payment data: {}", paymentData);

        paymentService.processPayment(paymentData, signature);

        return ResponseEntity.ok("Payment data received successfully");
    }
}
