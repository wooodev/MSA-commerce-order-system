package com.commerce.order.infrastructure.kafka.consumer

import com.commerce.order.domain.order.service.OrderService
import com.commerce.order.infrastructure.kafka.event.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

/**
 * Saga 이벤트 소비자.
 * payment-events, stock-events 토픽을 구독하여 보상 트랜잭션 처리.
 */
@Component
class SagaEventConsumer(
    private val orderService: OrderService,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [KafkaTopics.PAYMENT_EVENTS],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun consumePaymentEvent(message: String) {
        val eventType = objectMapper.readTree(message).get("eventType")?.asText() ?: run {
            log.warn("eventType 필드 없음: $message")
            return
        }

        runCatching {
            when (eventType) {
                "PAYMENT_COMPLETED" -> {
                    val payload = objectMapper.readValue(message, PaymentCompletedPayload::class.java)
                    orderService.handlePaymentCompleted(payload.orderId)
                    log.info("결제 완료 처리: orderId=${payload.orderId}")
                }
                "PAYMENT_FAILED" -> {
                    val payload = objectMapper.readValue(message, PaymentFailedPayload::class.java)
                    orderService.handlePaymentFailed(payload.orderId)
                    log.warn("결제 실패 처리 (보상 트랜잭션): orderId=${payload.orderId}, reason=${payload.reason}")
                }
                else -> log.debug("처리하지 않는 이벤트 타입: $eventType")
            }
        }.onFailure { e ->
            log.error("결제 이벤트 처리 실패: eventType=$eventType", e)
            throw e // DLQ로 이동
        }
    }

    @KafkaListener(
        topics = [KafkaTopics.STOCK_EVENTS],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun consumeStockEvent(message: String) {
        val eventType = objectMapper.readTree(message).get("eventType")?.asText() ?: run {
            log.warn("eventType 필드 없음: $message")
            return
        }

        runCatching {
            when (eventType) {
                "STOCK_DEDUCTED" -> {
                    val payload = objectMapper.readValue(message, StockDeductedPayload::class.java)
                    orderService.handleStockDeducted(payload.orderId)
                    log.info("재고 차감 완료 처리: orderId=${payload.orderId}")
                }
                "STOCK_FAILED" -> {
                    val payload = objectMapper.readValue(message, StockFailedPayload::class.java)
                    orderService.handleStockFailed(payload.orderId)
                    log.warn("재고 차감 실패 처리 (보상 트랜잭션): orderId=${payload.orderId}, productId=${payload.productId}")
                }
                else -> log.debug("처리하지 않는 이벤트 타입: $eventType")
            }
        }.onFailure { e ->
            log.error("재고 이벤트 처리 실패: eventType=$eventType", e)
            throw e
        }
    }
}
