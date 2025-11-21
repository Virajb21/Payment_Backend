import asyncio
import aiohttp
import uuid
import random
from datetime import datetime, timezone


TARGET_URL = "http://localhost:8081/api/payment/receive"
CONCURRENCY = 2      # number of parallel workers
TOTAL_REQUESTS = 100  # total requests to send

# -----------------------------
# Generate Dynamic Payment JSON
# -----------------------------
def generate_payment_json():
    return {
        "transactionId": str(uuid.uuid4()),
        "orderId": f"ORD-{random.randint(10000, 99999)}",
        "payerName": random.choice(["Viraj", "Amit", "Sneha", "Kunal", "Riya"]),
        "amount": round(random.uniform(100, 5000), 2),
        "currency": "INR",
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "source": random.choice(["MOBILE_APP", "WEB_PORTAL"]),
        "paymentMethod": random.choice(["UPI", "CARD", "NETBANKING"]),
        "geoLocation": {
            "lat": round(random.uniform(-90, 90), 6),
            "lon": round(random.uniform(-180, 180), 6)
        }
    }


# -----------------------------
# Send Request
# -----------------------------
async def send_request(session, req_id):
    payload = generate_payment_json()

    # DEBUG LOG (remove later)
    print(f"[DEBUG] Sending request #{req_id}: {payload}")

    try:
        async with session.post(TARGET_URL, json=payload) as response:
            status = response.status
            text = await response.text()
            return status, text
    except Exception as e:
        return "ERROR", str(e)


# -----------------------------
# Worker for concurrency
# -----------------------------
async def worker(worker_id, session, queue):
    while True:
        req_id = await queue.get()
        if req_id is None:
            break
        status, result = await send_request(session, req_id)
        print(f"Worker {worker_id} → Req {req_id} → Status: {status}")
        queue.task_done()


# -----------------------------
# Main orchestrator
# -----------------------------
async def main():
    queue = asyncio.Queue()

    # enqueue tasks
    for i in range(1, TOTAL_REQUESTS + 1):
        queue.put_nowait(i)

    async with aiohttp.ClientSession() as session:
        workers = [
            asyncio.create_task(worker(i, session, queue))
            for i in range(CONCURRENCY)
        ]

        await queue.join()

        for _ in workers:
            queue.put_nowait(None)

        for w in workers:
            await w


if __name__ == "__main__":
    asyncio.run(main())
