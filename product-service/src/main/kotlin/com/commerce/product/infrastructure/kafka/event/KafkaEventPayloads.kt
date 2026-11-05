package com.commerce.product.infrastructure.kafka.event

import java.math.BigDecimal

// ───────── product-service 소비 이벤트 ─────────

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

data class PaymentCompletedPayload(
    val eventType: String,
    val paymentId: Long,
    val orderId: Long,
    val userId: Long,
    val amount: BigDecimal,
    val items: List<OrderItemPayload>  // ORDER_CREATED에서 전달받은 항목
)

// ───────── product-service 발행 이벤트 ─────────

data class StockDeductedPayload(
    val eventType: String = "STOCK_DEDUCTED",
    val orderId: Long,
    val items: List<StockItemPayload>
)

data class StockItemPayload(
    val productId: Long,
    val quantity: Int
)

data class StockFailedPayload(
    val eventType: String = "STOCK_FAILED",
    val orderId: Long,
    val productId: Long,
    val reason: String
)

data class NotifyUserPayload(
    val eventType: String = "NOTIFY_USER",
    val userId: Long,
    val type: String,
    val title: String,
    val content: String
)
