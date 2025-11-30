package com.example.payment.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.payment.dto.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.payment.service.PaymentService;

@RestController
@RequestMapping("/api/payment")

public class PaymentController {
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @PostMapping("/receive")
    public ResponseEntity<String> receivePayment(@Valid @RequestBody PaymentRequest paymentData ,@RequestHeader("X-Signature") String signature) {
        // Process the payment data (this is just a placeholder)
        log.debug("Received payment data: {}", paymentData);

        paymentService.processPayment(paymentData, signature);

        return ResponseEntity.ok("Payment data received successfully");
    }

}
