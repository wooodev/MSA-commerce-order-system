package com.commerce.payment.domain.payment.dto

import com.commerce.payment.domain.payment.entity.Payment
import com.commerce.payment.domain.payment.enums.PaymentStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentResponse(
    val id: Long,
    val orderId: Long,
    val userId: Long,
    val amount: BigDecimal,
    val status: PaymentStatus,
    val createdAt: LocalDateTime
)

fun Payment.toResponse(): PaymentResponse = PaymentResponse(
    id = id,
    orderId = orderId,
    userId = userId,
    amount = amount,
    status = status,
    createdAt = createdAt
)
