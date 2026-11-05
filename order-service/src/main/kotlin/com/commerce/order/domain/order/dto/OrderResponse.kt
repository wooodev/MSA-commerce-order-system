package com.commerce.order.domain.order.dto

import com.commerce.order.domain.order.entity.Order
import com.commerce.order.domain.order.entity.OrderItem
import com.commerce.order.domain.order.enums.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponse(
    val id: Long,
    val userId: Long,
    val status: OrderStatus,
    val totalAmount: BigDecimal,
    val items: List<OrderItemResponse>,
    val createdAt: LocalDateTime
)

data class OrderItemResponse(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val price: BigDecimal,
    val subtotal: BigDecimal
)

// ───────── 변환 확장 함수 ─────────

fun Order.toResponse(): OrderResponse = OrderResponse(
    id = id,
    userId = userId,
    status = status,
    totalAmount = totalAmount,
    items = items.map { it.toResponse() },
    createdAt = createdAt
)

fun OrderItem.toResponse(): OrderItemResponse = OrderItemResponse(
    id = id,
    productId = productId,
    quantity = quantity,
    price = price,
    subtotal = subtotal
)
