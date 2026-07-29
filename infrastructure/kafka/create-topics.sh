#!/usr/bin/env bash
set -euo pipefail

BOOTSTRAP_SERVER="${KAFKA_BOOTSTRAP_SERVER:-kafka:29092}"
KAFKA_TOPICS="/opt/kafka/bin/kafka-topics.sh"

create_topic() {
  local topic="$1"
  local partitions="$2"
  local retention_ms="$3"

  "${KAFKA_TOPICS}" \
    --bootstrap-server "${BOOTSTRAP_SERVER}" \
    --create \
    --if-not-exists \
    --topic "${topic}" \
    --partitions "${partitions}" \
    --replication-factor 1 \
    --config "retention.ms=${retention_ms}"
}

# Core blueprint topics
create_topic "transactions.completed" 3 604800000
create_topic "fraud.flagged" 3 2592000000
create_topic "interest.credited" 2 604800000
create_topic "audit.transaction" 3 7776000000
create_topic "user.registered" 2 604800000

# Retry and dead-letter topics prepared for Phase 7
create_topic "transactions.completed.retry" 3 604800000
create_topic "transactions.completed.dlq" 3 2592000000
create_topic "fraud.flagged.retry" 3 604800000
create_topic "fraud.flagged.dlq" 3 2592000000

echo "Kafka topics are ready:"
"${KAFKA_TOPICS}" --bootstrap-server "${BOOTSTRAP_SERVER}" --list
