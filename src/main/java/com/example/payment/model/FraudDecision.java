package com.example.payment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "fraud_decisions")
public class FraudDecision {

    @Id
    private String id;

    private String transactionId;
    private double riskScore;
    private DecisionType decision;
    private String reason;
    private Instant decidedAt;

    public FraudDecision(String transactionId,
                         double riskScore,
                         DecisionType decision,
                         String reason) {
        this.transactionId = transactionId;
        this.riskScore = riskScore;
        this.decision = decision;
        this.reason = reason;
        this.decidedAt = Instant.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public DecisionType getDecision() {
        return decision;
    }

    public String getReason() {
        return reason;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }
}
