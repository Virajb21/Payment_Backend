package com.example.payment.service;

import com.example.payment.model.DecisionType;
import com.example.payment.model.FraudDecision;
import com.example.payment.repository.FraudDecisionRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FraudDecisionService {

    private final FraudDecisionRepository repository;

    public FraudDecisionService(FraudDecisionRepository repository) {
        this.repository = repository;
    }

    public FraudDecision evaluate(Map<String, Object> scoredEvent) {

        String transactionId = (String) scoredEvent.get("transactionId");
        double riskScore = ((Number) scoredEvent.get("risk_score")).doubleValue();
        double amount = ((Number) scoredEvent.get("amount")).doubleValue();

        FraudDecision decision;

        if (riskScore >= 0.8) {
            decision = new FraudDecision(
                    transactionId, riskScore,
                    DecisionType.BLOCK,
                    "High risk score"
            );
        } else if (riskScore >= 0.5 && amount > 50_000) {
            decision = new FraudDecision(
                    transactionId, riskScore,
                    DecisionType.REVIEW,
                    "Medium risk with high amount"
            );
        } else {
            decision = new FraudDecision(
                    transactionId, riskScore,
                    DecisionType.PASS,
                    "Low risk"
            );
        }

        // ✅ Persist decision
        return repository.save(decision);
    }
}
