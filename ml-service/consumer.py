from kafka import KafkaConsumer, KafkaProducer
import json
from datetime import datetime

RAW_TOPIC = "transactions.raw"
SCORED_TOPIC = "transactions.scored"
BOOTSTRAP_SERVERS = "localhost:9092"

# -----------------------
# Kafka Consumer
# -----------------------
consumer = KafkaConsumer(
    RAW_TOPIC,
    bootstrap_servers=BOOTSTRAP_SERVERS,
    value_deserializer=lambda m: json.loads(m.decode("utf-8")),
    auto_offset_reset="earliest",
    enable_auto_commit=True,
    group_id="ml-service-group"
)

# -----------------------
# Kafka Producer
# -----------------------
producer = KafkaProducer(
    bootstrap_servers=BOOTSTRAP_SERVERS,
    value_serializer=lambda v: json.dumps(v).encode("utf-8")
)

print("🚀 ML Service started (raw → scored)")

# -----------------------
# Feature Engineering + Scoring
# -----------------------
def enrich_and_score(event: dict) -> dict:
    amount = event.get("amount", 0)
    currency = event.get("currency", "UNKNOWN")
    payment_method = event.get("paymentMethod", "UNKNOWN")

    # Feature engineering
    is_high_amount = amount > 10_000
    is_foreign_currency = currency != "INR"

    hour_of_day = datetime.utcnow().hour

    # Dummy risk score
    risk_score = 0.1
    if is_high_amount:
        risk_score += 0.5
    if is_foreign_currency:
        risk_score += 0.3

    risk_score = min(risk_score, 1.0)

    # Enriched event
    return {
        **event,
        "features": {
            "is_high_amount": is_high_amount,
            "is_foreign_currency": is_foreign_currency,
            "hour_of_day": hour_of_day
        },
        "risk_score": risk_score,
        "scored_at": datetime.utcnow().isoformat() + "Z"
    }


# -----------------------
# Consume → Process → Produce
# -----------------------
for message in consumer:
    raw_event = message.value

    print("📥 Received raw event:", raw_event.get("transactionId"))

    scored_event = enrich_and_score(raw_event)

    producer.send(
        SCORED_TOPIC,
        key=raw_event.get("transactionId", "").encode("utf-8"),
        value=scored_event
    )

    print(
        f"📤 Published scored event {raw_event.get('transactionId')} "
        f"with risk_score={scored_event['risk_score']}"
    )
