package com.example.payment.controller;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.payment.dto.PaymentRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @PostMapping("/receive")
    public ResponseEntity<String> receivePayment(@Valid @RequestBody PaymentRequest paymentData) {
        // Process the payment data (this is just a placeholder)
        System.out.println("Received payment data: " + paymentData.getOrderId());
        return ResponseEntity.ok("Payment received successfully");
    }

}
