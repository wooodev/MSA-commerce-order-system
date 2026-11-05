-- 서비스별 독립 스키마 생성
CREATE DATABASE IF NOT EXISTS order_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS product_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS payment_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS notification_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- commerce 사용자에게 각 DB 권한 부여
GRANT ALL PRIVILEGES ON order_db.*        TO 'commerce'@'%';
GRANT ALL PRIVILEGES ON product_db.*      TO 'commerce'@'%';
GRANT ALL PRIVILEGES ON payment_db.*      TO 'commerce'@'%';
GRANT ALL PRIVILEGES ON notification_db.* TO 'commerce'@'%';

FLUSH PRIVILEGES;
