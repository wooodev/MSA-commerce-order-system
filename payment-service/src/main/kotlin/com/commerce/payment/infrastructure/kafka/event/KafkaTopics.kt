package com.commerce.payment.infrastructure.kafka.event

object KafkaTopics {
    const val ORDER_EVENTS        = "order-events"
    const val PAYMENT_EVENTS      = "payment-events"
    const val STOCK_EVENTS        = "stock-events"
    const val NOTIFICATION_EVENTS = "notification-events"

    const val ORDER_EVENTS_DLQ   = "order-events.dlq"
    const val STOCK_EVENTS_DLQ   = "stock-events.dlq"
}
