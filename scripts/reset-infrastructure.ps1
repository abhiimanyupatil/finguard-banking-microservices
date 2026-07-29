$ErrorActionPreference = "Stop"
Write-Host "This deletes all local MySQL, Redis, and Kafka data." -ForegroundColor Yellow
docker compose down -v --remove-orphans
docker compose up -d
