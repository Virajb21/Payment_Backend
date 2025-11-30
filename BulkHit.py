import asyncio
import aiohttp
import uuid
import random
import hmac
import hashlib
import base64
import json
from datetime import datetime, timezone

TARGET_URL = "http://localhost:8081/api/payment/receive"
CONCURRENCY = 2
TOTAL_REQUESTS = 10

SECRET_KEY = "mySuperSecretKey123"


# ------------------------------------
# HMAC based on EXACT JSON sent (no sorting)
# ------------------------------------
def create_signature(payload: dict) -> str:
    normalized = json.dumps(payload, separators=(',', ':'))  # no sort_keys
    digest = hmac.new(
        SECRET_KEY.encode(),
        normalized.encode(),
        hashlib.sha256
    ).digest()
    return base64.b64encode(digest).decode()


# ------------------------------------
# Generate Dynamic Payment JSON
# ------------------------------------
def generate_payment_json():
    return {
        "orderId": f"ORD-{random.randint(10000, 99999)}",
        "payerName": random.choice(["Viraj", "Amit", "Sneha", "Kunal", "Riya"]),
        "amount": round(random.uniform(100, 5000), 2),
        "currency": "INR",
        "timestamp": datetime.now(timezone.utc).isoformat(timespec="milliseconds").replace("+00:00", "Z"),
        "source": random.choice(["MOBILE_APP", "WEB_PORTAL"]),
        "paymentMethod": random.choice(["UPI", "CARD", "NETBANKING"]),
        "geoLocation": {
            "lat": round(random.uniform(-90, 90), 6),
            "lon": round(random.uniform(-180, 180), 6)
        },
        "transactionId": str(uuid.uuid4())
    }


# ------------------------------------
# Send Request
# ------------------------------------
async def send_request(session, req_id):
    payload = generate_payment_json()

    signature = create_signature(payload)

    # DEBUG — prints exactly what backend should HMAC
    print(f"[DEBUG] JSON used for HMAC #{req_id}: {json.dumps(payload, separators=(',', ':'))}")
    print(f"[DEBUG] Signature #{req_id}: {signature}")

    headers = {"X-SIGNATURE": signature}

    try:
        async with session.post(TARGET_URL, json=payload, headers=headers) as response:
            text = await response.text()
            return response.status, text
    except Exception as e:
        return "ERROR", str(e)


# ------------------------------------
# Worker
# ------------------------------------
async def worker(worker_id, session, queue):
    while True:
        req_id = await queue.get()
        if req_id is None:
            break
        status, result = await send_request(session, req_id)
        print(f"Worker {worker_id} → Req {req_id} → Status: {status} → Response: {result}")
        queue.task_done()


# ------------------------------------
# Main
# ------------------------------------
async def main():
    queue = asyncio.Queue()
    for i in range(1, TOTAL_REQUESTS + 1):
        queue.put_nowait(i)

    async with aiohttp.ClientSession() as session:
        workers = [asyncio.create_task(worker(i, session, queue)) for i in range(CONCURRENCY)]

        await queue.join()

        for _ in workers:
            queue.put_nowait(None)
        for w in workers:
            await w


if __name__ == "__main__":
    asyncio.run(main())
