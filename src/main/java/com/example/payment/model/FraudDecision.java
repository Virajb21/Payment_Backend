package com.example.payment.model;

import java.time.Instant;

public class FraudDecision {

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
