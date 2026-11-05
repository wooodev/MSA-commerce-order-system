package com.commerce.order.infrastructure.kafka.event

import java.math.BigDecimal

// ───────── order-service 발행 이벤트 ─────────

data class OrderCreatedPayload(
    val eventType: String = "ORDER_CREATED",
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

data class OrderCancelledPayload(
    val eventType: String = "ORDER_CANCELLED",
    val orderId: Long,
    val userId: Long,
    val reason: String
)

// ───────── order-service 소비 이벤트 (payment-service 발행) ─────────

data class PaymentCompletedPayload(
    val eventType: String,
    val paymentId: Long,
    val orderId: Long,
    val userId: Long,
    val amount: BigDecimal,
    val items: List<OrderItemPayload>
)

data class PaymentFailedPayload(
    val eventType: String,
    val orderId: Long,
    val userId: Long,
    val reason: String
)

// ───────── order-service 소비 이벤트 (product-service 발행) ─────────

data class StockDeductedPayload(
    val eventType: String,
    val orderId: Long,
    val items: List<StockItemPayload>
)

data class StockItemPayload(
    val productId: Long,
    val quantity: Int
)

data class StockFailedPayload(
    val eventType: String,
    val orderId: Long,
    val productId: Long,
    val reason: String
)

// ───────── notification-events 발행 ─────────

data class NotifyUserPayload(
    val eventType: String = "NOTIFY_USER",
    val userId: Long,
    val type: String,
    val title: String,
    val content: String
)
