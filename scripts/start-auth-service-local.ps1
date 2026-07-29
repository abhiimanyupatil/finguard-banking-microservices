$ErrorActionPreference = "Stop"

$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$envFile = Join-Path $projectRoot ".env"

if (-not (Test-Path $envFile)) {
    throw "Missing .env. Create it with: Copy-Item .env.example .env"
}

function Get-DotEnvValue {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Name
    )

    $line = Get-Content $envFile |
        Where-Object { $_ -match "^$([regex]::Escape($Name))=" } |
        Select-Object -First 1

    if (-not $line) {
        throw "Missing $Name in .env"
    }

    return ($line -split "=", 2)[1]
}

$mysqlHostPort = Get-DotEnvValue -Name "MYSQL_HOST_PORT"
$authDbPassword = Get-DotEnvValue -Name "AUTH_DB_PASSWORD"

$env:AUTH_DB_USERNAME = "auth_user"
$env:AUTH_DB_PASSWORD = $authDbPassword
$env:AUTH_DB_URL = "jdbc:mysql://localhost:$mysqlHostPort/auth_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata&characterEncoding=UTF-8"
$env:SERVER_PORT = "8081"

Push-Location $projectRoot
try {
    Write-Host "Starting FinGuard Auth Service on http://localhost:8081" -ForegroundColor Green
    Write-Host "Health endpoint: http://localhost:8081/actuator/health" -ForegroundColor Cyan
    mvn -pl auth-service spring-boot:run
}
finally {
    Pop-Location
}
