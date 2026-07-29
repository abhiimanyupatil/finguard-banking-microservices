#!/usr/bin/env bash
set -euo pipefail

mysql --protocol=socket -uroot -p"${MYSQL_ROOT_PASSWORD}" <<-EOSQL
    CREATE DATABASE IF NOT EXISTS auth_db
        CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
    CREATE DATABASE IF NOT EXISTS account_db
        CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
    CREATE DATABASE IF NOT EXISTS transaction_db
        CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
    CREATE DATABASE IF NOT EXISTS notification_db
        CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
    CREATE DATABASE IF NOT EXISTS audit_db
        CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

    CREATE USER IF NOT EXISTS 'auth_user'@'%' IDENTIFIED BY '${AUTH_DB_PASSWORD}';
    CREATE USER IF NOT EXISTS 'account_user'@'%' IDENTIFIED BY '${ACCOUNT_DB_PASSWORD}';
    CREATE USER IF NOT EXISTS 'transaction_user'@'%' IDENTIFIED BY '${TRANSACTION_DB_PASSWORD}';
    CREATE USER IF NOT EXISTS 'notification_user'@'%' IDENTIFIED BY '${NOTIFICATION_DB_PASSWORD}';
    CREATE USER IF NOT EXISTS 'audit_user'@'%' IDENTIFIED BY '${AUDIT_DB_PASSWORD}';

    GRANT ALL PRIVILEGES ON auth_db.* TO 'auth_user'@'%';
    GRANT ALL PRIVILEGES ON account_db.* TO 'account_user'@'%';
    GRANT ALL PRIVILEGES ON transaction_db.* TO 'transaction_user'@'%';
    GRANT ALL PRIVILEGES ON notification_db.* TO 'notification_user'@'%';
    GRANT SELECT, INSERT ON audit_db.* TO 'audit_user'@'%';

    FLUSH PRIVILEGES;
EOSQL

echo "Created Phase 0 databases and least-privilege service users."
