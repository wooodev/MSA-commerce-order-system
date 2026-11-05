CREATE TABLE products
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description TEXT           NULL,
    price       DECIMAL(19, 2) NOT NULL,
    created_at  DATETIME(6)    NOT NULL,
    updated_at  DATETIME(6)    NOT NULL,
    deleted_at  DATETIME(6)    NULL,
    INDEX idx_products_name (name)
);

-- 재고는 Product와 1:1 관계, 분리 테이블로 락 경쟁 최소화
CREATE TABLE stocks
(
    id         BIGINT     AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT     NOT NULL UNIQUE,
    quantity   INT        NOT NULL DEFAULT 0,
    version    BIGINT     NOT NULL DEFAULT 0,  -- 낙관적 락 (분산 락의 보조 수단)
    updated_at DATETIME(6) NOT NULL,
    INDEX idx_stocks_product_id (product_id)
);

CREATE TABLE outbox_events
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type   VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    topic        VARCHAR(255) NOT NULL,
    payload      TEXT         NOT NULL,
    published    TINYINT(1)   NOT NULL DEFAULT 0,
    created_at   DATETIME(6)  NOT NULL,
    published_at DATETIME(6)  NULL,
    INDEX idx_outbox_published_created (published, created_at)
);
