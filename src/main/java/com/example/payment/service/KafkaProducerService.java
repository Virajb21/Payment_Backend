package com.example.payment.service;

import com.example.payment.dto.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaProducerService.class);

    private static final String TOPIC = "transactions.raw";

    private final KafkaTemplate<String, PaymentRequest> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, PaymentRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTransaction(PaymentRequest request) {

        String key = request.getTransactionId();

        kafkaTemplate.send(TOPIC, key, request);

        log.info("📤 Published transaction {} to Kafka topic {}",
                request.getTransactionId(), TOPIC);
    }
}
