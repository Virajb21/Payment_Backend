package com.example.payment.consumer;

import com.example.payment.model.FraudDecision;
import com.example.payment.service.FraudDecisionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FraudDecisionConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(FraudDecisionConsumer.class);

    private final FraudDecisionService fraudDecisionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FraudDecisionConsumer(FraudDecisionService fraudDecisionService) {
        this.fraudDecisionService = fraudDecisionService;
    }

    @KafkaListener(
            topics = "transactions.scored",
            groupId = "fraud-decision-engine"
    )
    public void consume(ConsumerRecord<String, String> record) {
        try {
            Map<String, Object> scoredEvent =
                    objectMapper.readValue(record.value(), Map.class);

            FraudDecision decision =
                    fraudDecisionService.evaluate(scoredEvent);

            log.info("🚨 Fraud Decision → txn={}, score={}, decision={}, reason={}",
                    decision.getTransactionId(),
                    decision.getRiskScore(),
                    decision.getDecision(),
                    decision.getReason()
            );

            // NEXT (later):
            // - persist to DB
            // - publish to another Kafka topic
        } catch (Exception e) {
            log.error("❌ Failed to process scored event", e);
        }
    }
}
