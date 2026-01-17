# 🚀 Fintech Event-Driven Payment & Fraud Detection Platform

An end-to-end **event-driven fintech backend system** built using  
**Spring Boot, Apache Kafka, Python ML services, Redis, and MongoDB**.

This project demonstrates **real-world backend architecture** for secure payment ingestion, ML-based risk scoring, and fraud decisioning.

---

## 🧠 High-Level Architecture

```
Client / App
    ↓
Payment Service (Spring Boot)
  - Validation
  - HMAC Authentication
  - Idempotency (Redis)
  - Rate Limiting
  - Kafka Producer
    ↓
Kafka Topic: transactions.raw
    ↓
ML Service (Python)
  - Feature Engineering
  - Risk Scoring
  - Kafka Producer
    ↓
Kafka Topic: transactions.scored
    ↓
Fraud Decision Engine (Spring Boot)
  - Rule-based Decisions
  - PASS / REVIEW / BLOCK
  - MongoDB Persistence
```

---

## 📦 Repository Structure (Actual)

```
Fintech_Backend/
│
├── src/                     # Spring Boot application (payment + fraud)
│   ├── controller           # Payment API
│   ├── service              # Payment, Fraud, Idempotency, Rate Limiting
│   ├── consumer             # Kafka fraud decision consumer
│   └── model                # Domain models
│
├── ml-service/              # Python ML microservice
│   ├── consumer.py          # Kafka consumer + producer
│   └── requirements.txt
│
├── infra/                   # Infrastructure configs
│   └── docker-compose.yml   # Kafka, Redis, MongoDB
│
├── bulk_hit/                # Load / stress testing scripts
│
├── pom.xml                  # Maven config
├── mvnw / mvnw.cmd          # Maven wrapper
├── docker-compose.yml       # Local infra bootstrap
└── README.md
```

---

## 🔐 Payment & Fraud Service (Spring Boot)

### Responsibilities
- Secure payment ingestion
- Prevent replay attacks & duplicates
- Control request rate
- Publish events asynchronously
- Make final fraud decisions

### Implemented Features
- **HMAC authentication**
- **Timestamp validation**
- **Idempotency using Redis**
- **Rate limiting**
- **Kafka producer (`transactions.raw`)**
- **Kafka consumer (`transactions.scored`)**
- **Fraud rule engine**
- **MongoDB persistence**

---

## 📊 ML Service (Python)

### Responsibilities
- Consume raw transaction events
- Perform feature engineering
- Generate risk scores
- Publish enriched events

### Features
- Kafka consumer (`transactions.raw`)
- Lightweight ML / rule-based scoring
- Kafka producer (`transactions.scored`)
- Fully decoupled from business logic

> ML service **only scores** — it never blocks transactions.

---

## 🧠 Fraud Decision Engine

### Responsibilities
- Consume scored transactions
- Apply deterministic fraud rules
- Generate final decision
- Persist decisions for audit

### Decision Logic
```
IF risk_score ≥ 0.8 → BLOCK
IF risk_score ≥ 0.5 AND amount > 50,000 → REVIEW
ELSE → PASS
```

### Persistence
- MongoDB
- Collection: `fraud_decisions`

---

## 🗄️ Data Stores

### Redis
- Used for **idempotency**
- Prevents duplicate request processing

### MongoDB
- Stores fraud decisions
- Enables audit & analytics

All databases run via **Docker for local development**.

---

## 🔄 Event-Driven Design

- Kafka is the **system backbone**
- Services communicate asynchronously
- Loose coupling between API, ML, and fraud logic
- Scales horizontally

---

## 🧪 Local Development Setup

### Prerequisites
- Java 17+
- Python 3.10+
- Docker & Docker Compose

### Start Infrastructure
```bash
docker-compose up -d
```

### Run Services
- Start Spring Boot application
- Start ML service:
```bash
pip install -r requirements.txt
python consumer.py
```

---

## 📈 Load Testing

The `bulk_hit/` folder contains scripts to:
- Generate high request volume
- Test idempotency
- Validate rate limiting
- Stress Kafka ingestion

---

## ✅ What This Project Demonstrates

- Event-driven microservice architecture
- Secure API design
- Kafka-based streaming pipelines
- ML scoring separated from business decisions
- Production-style fraud detection
- Clean service boundaries

---