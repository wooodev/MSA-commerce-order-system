package com.commerce.payment.infrastructure.kafka.consumer

import com.commerce.payment.domain.payment.service.PaymentService
import com.commerce.payment.infrastructure.kafka.event.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

/**
 * Saga 이벤트 소비자.
 * ORDER_CREATED → 결제 처리
 * STOCK_FAILED  → 환불 (보상 트랜잭션)
 */
@Component
class SagaEventConsumer(
    private val paymentService: PaymentService,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [KafkaTopics.ORDER_EVENTS],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun consumeOrderEvent(message: String) {
        val eventType = objectMapper.readTree(message).get("eventType")?.asText() ?: return

        runCatching {
            when (eventType) {
                "ORDER_CREATED" -> {
                    val payload = objectMapper.readValue(message, OrderCreatedPayload::class.java)
                    paymentService.processPayment(
                        orderId = payload.orderId,
                        userId = payload.userId,
                        amount = payload.totalAmount,
                        items = payload.items
                    )
                    log.info("결제 처리 시작: orderId=${payload.orderId}")
                }
                else -> log.debug("처리하지 않는 이벤트: $eventType")
            }
        }.onFailure { e ->
            log.error("주문 이벤트 처리 실패: eventType=$eventType", e)
            throw e
        }
    }

    @KafkaListener(
        topics = [KafkaTopics.STOCK_EVENTS],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun consumeStockEvent(message: String) {
        val eventType = objectMapper.readTree(message).get("eventType")?.asText() ?: return

        runCatching {
            when (eventType) {
                "STOCK_FAILED" -> {
                    val payload = objectMapper.readValue(message, StockFailedPayload::class.java)
                    // 보상 트랜잭션: 재고 차감 실패 → 결제 환불
                    paymentService.refundPayment(payload.orderId)
                    log.warn("환불 처리 (보상 트랜잭션): orderId=${payload.orderId}")
                }
                else -> log.debug("처리하지 않는 이벤트: $eventType")
            }
        }.onFailure { e ->
            log.error("재고 이벤트 처리 실패: eventType=$eventType", e)
            throw e
        }
    }
}
