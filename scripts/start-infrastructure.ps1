$ErrorActionPreference = "Stop"

if (-not (Test-Path ".env")) {
    Copy-Item ".env.example" ".env"
    Write-Host "Created .env from .env.example. Review passwords before production use." -ForegroundColor Yellow
}

docker compose up -d
docker compose ps

Write-Host "MySQL:  localhost:3306" -ForegroundColor Green
Write-Host "Redis: localhost:6379" -ForegroundColor Green
Write-Host "Kafka: localhost:9092" -ForegroundColor Green
Write-Host "Zipkin: http://localhost:9411" -ForegroundColor Green
