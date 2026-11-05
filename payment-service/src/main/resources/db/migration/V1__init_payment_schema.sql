CREATE TABLE payments
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT         NOT NULL UNIQUE,  -- 주문당 결제 1건 (멱등성 보장)
    user_id    BIGINT         NOT NULL,
    amount     DECIMAL(19, 2) NOT NULL,
    status     VARCHAR(50)    NOT NULL,
    created_at DATETIME(6)    NOT NULL,
    updated_at DATETIME(6)    NOT NULL,
    INDEX idx_payments_order_id (order_id),
    INDEX idx_payments_user_id (user_id)
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
