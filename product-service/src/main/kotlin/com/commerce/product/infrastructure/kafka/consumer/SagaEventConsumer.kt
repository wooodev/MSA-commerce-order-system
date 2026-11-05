package com.commerce.product.infrastructure.kafka.consumer

import com.commerce.product.domain.product.service.StockService
import com.commerce.product.infrastructure.kafka.event.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

/**
 * Saga 이벤트 소비자.
 * PAYMENT_COMPLETED 수신 → 재고 차감 (Redisson 분산 락)
 */
@Component
class SagaEventConsumer(
    private val stockService: StockService,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [KafkaTopics.PAYMENT_EVENTS],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun consumePaymentEvent(message: String) {
        val eventType = objectMapper.readTree(message).get("eventType")?.asText() ?: return

        runCatching {
            when (eventType) {
                "PAYMENT_COMPLETED" -> {
                    val payload = objectMapper.readValue(message, PaymentCompletedPayload::class.java)
                    // PAYMENT_COMPLETED에 담긴 items 정보로 재고 차감
                    stockService.deductStock(payload.orderId, payload.items)
                    log.info("재고 차감 시작: orderId=${payload.orderId}, items=${payload.items.size}건")
                }
                else -> log.debug("처리하지 않는 이벤트: $eventType")
            }
        }.onFailure { e ->
            log.error("결제 이벤트 처리 실패: eventType=$eventType", e)
            throw e
        }
    }
}
