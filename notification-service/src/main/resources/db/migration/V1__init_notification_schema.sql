CREATE TABLE notifications
(
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    type       VARCHAR(50)  NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    status     VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    created_at DATETIME(6)  NOT NULL,
    INDEX idx_notifications_user_id (user_id),
    INDEX idx_notifications_user_created (user_id, created_at)
);
