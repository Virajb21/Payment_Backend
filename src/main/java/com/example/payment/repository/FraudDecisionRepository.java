package com.example.payment.repository;

import com.example.payment.model.FraudDecision;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FraudDecisionRepository
        extends MongoRepository<FraudDecision, String> {
}
