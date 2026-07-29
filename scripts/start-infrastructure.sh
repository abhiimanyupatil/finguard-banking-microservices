#!/usr/bin/env bash
set -euo pipefail

if [[ ! -f .env ]]; then
  cp .env.example .env
  echo "Created .env from .env.example. Review passwords before production use."
fi

docker compose up -d
docker compose ps

printf '\nMySQL:  localhost:3306\nRedis:  localhost:6379\nKafka:  localhost:9092\nZipkin: http://localhost:9411\n'
