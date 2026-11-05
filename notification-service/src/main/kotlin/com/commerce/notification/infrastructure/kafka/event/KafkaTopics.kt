package com.commerce.notification.infrastructure.kafka.event

object KafkaTopics {
    const val NOTIFICATION_EVENTS     = "notification-events"
    const val NOTIFICATION_EVENTS_DLQ = "notification-events.dlq"
}
