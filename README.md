# FinGuard – Distributed Banking System with AI-Powered Fraud Detection

## Phase 0: Maven Workspace and Local Infrastructure

This workspace contains only the project foundation and local infrastructure. It intentionally does **not** generate all microservices together.

The final project will use Java and Spring Boot microservices, MySQL databases, Redis caching, Kafka events, Zipkin tracing, a Python fraud-detection service and a React frontend.

## What is included

```text
finguard-banking-microservices/
├── .mvn/
│   └── maven.config
├── docs/
│   └── PHASE_0_DECISIONS.md
├── infrastructure/
│   ├── kafka/
│   │   └── create-topics.sh
│   ├── mysql/
│   │   └── init/
│   │       └── 01-create-databases-and-users.sh
│   └── redis/
│       └── redis.conf
├── scripts/
│   ├── start-infrastructure.ps1
│   ├── stop-infrastructure.ps1
│   ├── reset-infrastructure.ps1
│   ├── start-infrastructure.sh
│   ├── stop-infrastructure.sh
│   └── reset-infrastructure.sh
├── .env.example
├── .gitignore
├── docker-compose.yml
├── FILE_MANIFEST.txt
├── pom.xml
└── README.md
```

## Prerequisites

Install:

- JDK 17
- Maven 3.9 or later
- Docker Desktop with Docker Compose
- Git
- IntelliJ IDEA, Eclipse/STS or VS Code

Verify:

```powershell
java -version
javac -version
mvn -version
docker --version
docker compose version
git --version
```

## 1. Prepare environment variables

Copy the environment template:

```powershell
Copy-Item .env.example .env
```

Open it:

```powershell
notepad .env
```

Change the development passwords and host ports when required.

The `.env` file contains local passwords and must never be uploaded to GitHub.

The `.env.example` file contains only sample configuration and should be uploaded.

## 2. Validate the parent Maven project

```powershell
mvn validate
```

Expected result:

```text
BUILD SUCCESS
```

The parent Maven project currently has no Java service modules. This is intentional.

## 3. Start the infrastructure

Using the PowerShell script:

```powershell
.\scripts\start-infrastructure.ps1
```

Or directly with Docker Compose:

```powershell
docker compose up -d
```

Check all containers:

```powershell
docker compose ps -a
```

Expected long-running containers:

```text
banking-mysql
banking-redis
banking-kafka
banking-zipkin
```

The `banking-kafka-init` container should finish with:

```text
Exited (0)
```

This is expected because it creates Kafka topics and then stops.

## 4. Verify MySQL

Open the MySQL command line:

```powershell
docker compose exec mysql mysql -uroot -p
```

Enter the Docker MySQL root password stored in `.env`.

Run:

```sql
SHOW DATABASES;
```

Expected project databases:

```text
auth_db
account_db
transaction_db
notification_db
audit_db
```

Exit:

```sql
EXIT;
```

## 5. Verify Redis

Use the Redis password stored in `.env`:

```powershell
docker compose exec redis redis-cli -a "YOUR_REDIS_PASSWORD" ping
```

Expected:

```text
PONG
```

## 6. Verify Kafka

List Kafka topics:

```powershell
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:29092 --list
```

Expected core topics:

```text
audit.transaction
fraud.flagged
interest.credited
transactions.completed
user.registered
```

Expected reliability topics:

```text
fraud.flagged.retry
fraud.flagged.dlq
transactions.completed.retry
transactions.completed.dlq
```

## 7. Verify Zipkin

Open Zipkin using the host port configured in `.env`.

Example:

```text
http://localhost:9412
```

The default port may be different on another team member's machine.

## Local host ports used on the current machine

```text
MySQL  → localhost:3308
Redis  → localhost:6380
Kafka  → localhost:9093
Zipkin → localhost:9412
```

These ports are used when an application runs directly on Windows.

## Internal Docker connection values

Future services running inside Docker will use:

```properties
spring.datasource.url=jdbc:mysql://mysql:3306/auth_db
spring.data.redis.host=redis
spring.data.redis.port=6379
spring.kafka.bootstrap-servers=kafka:29092
management.zipkin.tracing.endpoint=http://zipkin:9411/api/v2/spans
```

Docker services use container names and internal ports, not Windows host ports.

## Stop infrastructure

```powershell
docker compose down
```

This stops and removes containers but keeps database volumes.

To delete all MySQL, Redis and Kafka data:

```powershell
docker compose down -v
```

Use `-v` only when you intentionally want to reset all infrastructure data.

## Phase 0 completion checklist

- [x] JDK 17 installed and configured
- [x] Maven 3.9.16 installed and configured
- [x] Parent Maven project validated successfully
- [x] Docker Compose configuration validated
- [x] MySQL container is healthy
- [x] Redis container is healthy
- [x] Kafka container is healthy
- [x] Zipkin container is healthy
- [x] Five service databases were created
- [x] Kafka topics were created
- [x] Retry and dead-letter topics were created
- [x] `.env` is excluded from Git
- [x] `.env.example` is available for teammates
- [x] No application microservice has been generated prematurely

## Phase 0 result

Phase 0 created the technical foundation for FinGuard.

It prepared:

- The parent Maven workspace
- Java 17 and Maven configuration
- MySQL databases
- Redis caching infrastructure
- Kafka messaging infrastructure
- Retry and dead-letter Kafka topics
- Zipkin distributed tracing
- Docker Compose startup and reset scripts
- Secure local environment configuration

No authentication, account, transaction or AI business logic has been implemented yet.

## Next implementation step

Create only the `auth-service` Maven module.

The first Auth Service milestone will include:

- Spring Boot application
- Connection to `auth_db`
- Spring Data JPA configuration
- Spring Security dependency
- Actuator health endpoint
- Docker-compatible configuration
- Maven build and startup verification

Registration, login, JWT and refresh tokens will be implemented only after the Auth Service foundation works successfully.