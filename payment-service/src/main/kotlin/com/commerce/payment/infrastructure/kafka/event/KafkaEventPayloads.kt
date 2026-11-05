package com.commerce.payment.infrastructure.kafka.event

import java.math.BigDecimal

// ───────── payment-service 소비 이벤트 ─────────

data class OrderCreatedPayload(
    val eventType: String,
    val orderId: Long,
    val userId: Long,
    val totalAmount: BigDecimal,
    val items: List<OrderItemPayload>
)

data class OrderItemPayload(
    val productId: Long,
    val quantity: Int,
    val price: BigDecimal
)

data class StockFailedPayload(
    val eventType: String,
    val orderId: Long,
    val productId: Long,
    val reason: String
)

// ───────── payment-service 발행 이벤트 ─────────

data class PaymentCompletedPayload(
    val eventType: String = "PAYMENT_COMPLETED",
    val paymentId: Long,
    val orderId: Long,
    val userId: Long,
    val amount: BigDecimal,
    val items: List<OrderItemPayload>  // product-service가 재고 차감에 사용
)

data class PaymentFailedPayload(
    val eventType: String = "PAYMENT_FAILED",
    val orderId: Long,
    val userId: Long,
    val reason: String
)

data class NotifyUserPayload(
    val eventType: String = "NOTIFY_USER",
    val userId: Long,
    val type: String,
    val title: String,
    val content: String
)
