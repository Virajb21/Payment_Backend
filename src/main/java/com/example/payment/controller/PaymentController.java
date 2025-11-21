package com.example.payment.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @PostMapping("/receive")
    public ResponseEntity<String> receivePayment(@RequestBody String paymentData) {
        // Process the payment data (this is just a placeholder)
        System.out.println("Received payment data: " + paymentData);
        return ResponseEntity.ok("Payment received successfully");
    }

}
