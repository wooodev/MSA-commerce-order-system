package com.commerce.notification.infrastructure.kafka.consumer

import com.commerce.notification.domain.notification.service.NotificationServiceImpl
import com.commerce.notification.infrastructure.kafka.event.KafkaTopics
import com.commerce.notification.infrastructure.kafka.event.NotifyUserPayload
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NotificationEventConsumer(
    private val notificationService: NotificationServiceImpl,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [KafkaTopics.NOTIFICATION_EVENTS],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun consumeNotificationEvent(message: String) {
        val eventType = objectMapper.readTree(message).get("eventType")?.asText() ?: return

        runCatching {
            when (eventType) {
                "NOTIFY_USER" -> {
                    val payload = objectMapper.readValue(message, NotifyUserPayload::class.java)
                    notificationService.sendNotification(
                        userId = payload.userId,
                        type = payload.type,
                        title = payload.title,
                        content = payload.content
                    )
                }
                else -> log.debug("처리하지 않는 이벤트: $eventType")
            }
        }.onFailure { e ->
            log.error("알림 이벤트 처리 실패: eventType=$eventType", e)
            throw e
        }
    }
}
