# Phase 2A — Auth Service Foundation

## Scope

This phase creates only the Auth Service foundation:

- Spring Boot application
- Parent/child Maven module connection
- MySQL `auth_db` connectivity
- Spring Data JPA
- Spring Security dependency and minimal health-endpoint access policy
- Validation
- Actuator health endpoint
- H2-based context test
- Local PowerShell startup script
- Runtime Dockerfile

Registration, login, JWT, refresh tokens, entities, repositories, controllers and business services are intentionally excluded from this phase.

## Build and test

```powershell
mvn -pl auth-service -am clean test
```

## Start locally

Ensure Docker infrastructure is healthy, then run:

```powershell
.\scripts\start-auth-service-local.ps1
```

Open:

```text
http://localhost:8081/actuator/health
```

Expected result:

```json
{
  "status": "UP"
}
```
