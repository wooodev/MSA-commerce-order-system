package com.commerce.notification.infrastructure.kafka.event

data class NotifyUserPayload(
    val eventType: String,
    val userId: Long,
    val type: String,
    val title: String,
    val content: String
)
