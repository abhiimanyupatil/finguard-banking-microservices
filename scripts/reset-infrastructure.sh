#!/usr/bin/env bash
set -euo pipefail
echo "This deletes all local MySQL, Redis, and Kafka data."
docker compose down -v --remove-orphans
docker compose up -d
