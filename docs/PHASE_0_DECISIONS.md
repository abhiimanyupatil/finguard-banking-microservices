# Phase 0 Technical Decisions

## Scope

Phase 0 contains only:

- The root Maven parent project
- Version and dependency management
- Development environment conventions
- Infrastructure-only Docker Compose
- MySQL database/user initialization
- Redis configuration
- Kafka KRaft broker and topic initialization
- Zipkin server

No Spring Boot service module, entity, controller, repository, or frontend application has been generated yet.

## Version baseline

| Component | Version |
|---|---:|
| Java | 17 |
| Maven | 3.9+ |
| Spring Boot | 3.5.15 |
| Spring Cloud | 2025.0.3 |
| MySQL | 8.4.10 |
| Redis | 7.4.10 |
| Apache Kafka | 4.1.2, KRaft mode |
| Zipkin | 3.6 |

## Ports

| Component | Docker hostname | Internal port | Host port |
|---|---|---:|---:|
| MySQL | mysql | 3306 | 3306 |
| Redis | redis | 6379 | 6379 |
| Kafka | kafka | 29092 | 9092 |
| Kafka controller | kafka | 29093 | Not exposed |
| Zipkin | zipkin | 9411 | 9411 |

Spring Boot applications running in Docker will use `kafka:29092`. Applications running directly from IntelliJ or Eclipse will use `localhost:9092`.

## Database ownership

| Database | Application user |
|---|---|
| auth_db | auth_user |
| account_db | account_user |
| transaction_db | transaction_user |
| notification_db | notification_user |
| audit_db | audit_user |

The audit user receives only `SELECT` and `INSERT` access during Phase 0 to support append-only audit records.

## Maven module policy

Modules will be added one at a time. The first module will be `auth-service`; it will be added only when Phase 2 begins.
