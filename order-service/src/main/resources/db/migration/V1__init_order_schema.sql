CREATE TABLE orders
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT         NOT NULL,
    status       VARCHAR(50)    NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL,
    created_at   DATETIME(6)    NOT NULL,
    updated_at   DATETIME(6)    NOT NULL,
    deleted_at   DATETIME(6)    NULL,
    INDEX idx_orders_user_created (user_id, created_at)
);

CREATE TABLE order_items
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT         NOT NULL,
    product_id BIGINT         NOT NULL,
    quantity   INT            NOT NULL,
    price      DECIMAL(19, 2) NOT NULL,
    INDEX idx_order_items_order_id (order_id)
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
