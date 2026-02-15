## Notification-system

# 🚀 Distributed Notification Service (Production-Style Backend Project)

A **high-performance, scalable notification service** built with Spring Boot, Kafka, Redis, PostgreSQL, and Docker.
Designed to demonstrate **real backend engineering skills** for SDE-1 backend interviews.

This project simulates how production systems handle email/SMS/push notifications asynchronously with retries, caching, and worker services.

---

# 🧠 System Overview

This system accepts notification requests via REST APIs, processes them asynchronously using Kafka, and sends notifications through worker services.
It includes rate limiting, retry logic, dead-letter queues, metrics, and structured logging.

Designed to run locally or on a **single AWS EC2 free-tier machine** using Docker Compose.

---

# 🏗️ Architecture

```
Client
  │
  ▼
API Service (Spring Boot)
  │
  ├── PostgreSQL (store notifications)
  ├── Redis (rate limiting + dedup)
  └── Kafka Producer
          │
          ▼
        Kafka
          │
          ▼
Worker Service
  ├── Thread Pool
  ├── SendGrid Email Sender
  ├── Retry Logic
  ├── Dead Letter Queue
  └── Metrics + Logging
```

---

# ✨ Features

### Core

* REST API for creating notifications
* Async processing via Kafka
* Separate worker service
* Redis rate limiting & deduplication
* Thread-pool based concurrency
* Retry mechanism
* Dead-letter queue (DLQ)
* Status tracking table
* Structured logging
* Metrics via Spring Actuator
* Dockerized deployment
* AWS EC2 ready

### Notification Types

* Email (SendGrid integration)
* SMS (simulated)
* Push (simulated)

---

# 🛠️ Tech Stack

**Backend**

* Java 17
* Spring Boot
* Spring Data JPA

**Data**

* PostgreSQL
* Redis

**Messaging**

* Kafka

**DevOps**

* Docker
* Docker Compose

**Monitoring**

* Spring Boot Actuator
* Micrometer metrics

**Email**

* SendGrid

---

# 📂 Project Structure

```
notification-system/
 ├── notification-api-service/
 │    ├── controller/
 │    ├── service/
 │    ├── repository/
 │    ├── model/
 │    ├── dto/
 │    ├── config/
 │    └── queue/
 │
 ├── notification-worker-service/
 │    ├── consumer/
 │    ├── sender/
 │    ├── service/
 │    ├── repository/
 │    ├── model/
 │    ├── config/
 │    └── metrics/
 │
 ├── docker-compose.yml
 └── README.md
```

---

# 🗄️ Database Schema

### notifications

| column          | description         |
| --------------- | ------------------- |
| id              | primary key         |
| user_id         | user                |
| type            | EMAIL/SMS/PUSH      |
| message         | content             |
| priority        | priority            |
| recipient_email | email target        |
| recipient_phone | sms target          |
| status          | PENDING/SENT/FAILED |
| retry_count     | retry attempts      |
| created_at      | timestamp           |
| updated_at      | timestamp           |

### notification_status_logs

Tracks lifecycle events.

| column          | description |
| --------------- | ----------- |
| notification_id | FK          |
| status          | state       |
| error_message   | error       |
| created_at      | time        |

---

# 🔄 Event Flow

1. Client sends request → API
2. API validates + stores in DB
3. Redis checks rate limit
4. API publishes event to Kafka
5. Worker consumes event
6. Worker thread pool processes
7. SendGrid sends email
8. Success → update DB
9. Failure → retry
10. After max retries → DLQ

---

# 📡 API Endpoints

### Create Notification

```
POST /notifications
```

**Body**

```json
{
  "userId": 1,
  "type": "EMAIL",
  "message": "Order placed",
  "priority": "HIGH",
  "recipientEmail": "test@gmail.com"
}
```

---

### Get Notification by ID

```
GET /notifications/{id}
```

---

### Get User Notifications

```
GET /notifications/user/{userId}
```

---

# ⚡ Redis Usage

| Feature       | Purpose                 |
| ------------- | ----------------------- |
| Rate limiting | Max requests per user   |
| Deduplication | Prevent duplicate sends |
| Caching       | Future user preferences |

---

# 🧵 Concurrency Model

Worker service uses:

```
ExecutorService ThreadPool
core: 2
max: 5
queue: 100
```

Allows parallel processing of notifications.

---

# 🔁 Retry & DLQ

If sending fails:

```
retryCount < 3 → retry via Kafka
retryCount >= 3 → send to DLQ topic
```

DLQ topic: `notification-dlq`

Ensures reliability.

---

# 📊 Metrics

Actuator endpoints:

```
/actuator/health
/actuator/metrics
```

Custom metrics:

* notifications.sent
* notifications.failed
* notifications.retried

---

# 🐳 Running Locally (Docker)

### 1. Build jars

```
mvn clean package -DskipTests
```

### 2. Run system

```
docker compose up --build
```

### 3. Test API

```
POST http://localhost:8080/notifications
```

---

# ☁️ Deploy on AWS EC2 (Free Tier)

### 1. Launch EC2

Ubuntu t2.micro

### 2. Install Docker

```
sudo apt update
sudo apt install docker.io docker-compose -y
```

### 3. Run project

```
git clone <repo>
cd notification-system
docker compose up -d
```

Expose port 8080 in security group.

---

# 🧠 Imortant Talking Points
* Event-driven architecture
* Kafka async processing
* Worker thread pools
* Redis rate limiting
* Idempotency
* Retry & DLQ design
* SendGrid integration
* Metrics & logging
* Docker deployment
* Scaling strategy

---

# 📈 Scaling Strategy

To scale:

* Add more worker instances
* Increase Kafka partitions
* Use Redis cluster
* Use managed Kafka
* Deploy on Kubernetes

---

# 🔮 Future Improvements

* SMS provider integration
* Push notifications
* User preference service
* Prometheus + Grafana
* Kubernetes deployment
* Distributed tracing

---

# 👨‍💻 Author

Built as a **production-style backend system** for interview preparation and learning distributed systems.

---

# Demonstrates:

* Clean architecture
* Async systems
* Message queues
* Caching
* Concurrency
* Reliability patterns
* Observability
* Docker + Cloud readiness
