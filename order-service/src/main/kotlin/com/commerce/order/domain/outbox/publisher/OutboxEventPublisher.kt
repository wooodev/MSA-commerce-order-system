package com.commerce.order.domain.outbox.publisher

import com.commerce.order.domain.outbox.repository.OutboxEventRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Transactional Outbox 패턴 구현체.
 * 1초마다 미발행 이벤트를 Kafka로 발행하고 published=true로 업데이트.
 * Kafka 직접 발행 금지 — 반드시 이 퍼블리셔를 통해 발행.
 */
@Component
class OutboxEventPublisher(
    private val outboxEventRepository: OutboxEventRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedDelay = 1000)
    @Transactional
    fun publishPendingEvents() {
        val pendingEvents = outboxEventRepository.findTop100ByPublishedFalseOrderByCreatedAtAsc()

        pendingEvents.forEach { event ->
            runCatching {
                kafkaTemplate.send(event.topic, event.aggregateId, event.payload)
                event.markAsPublished()
            }.onFailure { e ->
                log.error(
                    "Outbox 이벤트 발행 실패 id={}, eventType={}, topic={}",
                    event.id, event.eventType, event.topic, e
                )
            }
        }
    }
}
