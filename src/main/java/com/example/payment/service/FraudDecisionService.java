package com.example.payment.service;

import com.example.payment.model.DecisionType;
import com.example.payment.model.FraudDecision;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FraudDecisionService {

    public FraudDecision evaluate(Map<String, Object> scoredEvent) {

        String transactionId = (String) scoredEvent.get("transactionId");
        double riskScore = ((Number) scoredEvent.get("risk_score")).doubleValue();
        double amount = ((Number) scoredEvent.get("amount")).doubleValue();

        if (riskScore >= 0.8) {
            return new FraudDecision(
                    transactionId,
                    riskScore,
                    DecisionType.BLOCK,
                    "High risk score"
            );
        }

        if (riskScore >= 0.5 && amount > 50_000) {
            return new FraudDecision(
                    transactionId,
                    riskScore,
                    DecisionType.REVIEW,
                    "Medium risk with high amount"
            );
        }

        return new FraudDecision(
                transactionId,
                riskScore,
                DecisionType.PASS,
                "Low risk"
        );
    }
}
