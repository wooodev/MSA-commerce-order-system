package com.commerce.payment.domain.outbox.publisher

import com.commerce.payment.domain.outbox.repository.OutboxEventRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OutboxEventPublisher(
    private val outboxEventRepository: OutboxEventRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedDelay = 1000)
    @Transactional
    fun publishPendingEvents() {
        outboxEventRepository.findTop100ByPublishedFalseOrderByCreatedAtAsc().forEach { event ->
            runCatching {
                kafkaTemplate.send(event.topic, event.aggregateId, event.payload)
                event.markAsPublished()
            }.onFailure { e ->
                log.error("Outbox 발행 실패: id=${event.id}, eventType=${event.eventType}", e)
            }
        }
    }
}
