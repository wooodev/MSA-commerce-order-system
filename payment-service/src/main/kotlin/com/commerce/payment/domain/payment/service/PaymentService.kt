package com.commerce.payment.domain.payment.service

import com.commerce.payment.domain.payment.dto.PaymentResponse
import com.commerce.payment.infrastructure.kafka.event.OrderItemPayload
import java.math.BigDecimal

interface PaymentService {
    fun processPayment(orderId: Long, userId: Long, amount: BigDecimal, items: List<OrderItemPayload>)
    fun refundPayment(orderId: Long)
    fun getPayment(orderId: Long): PaymentResponse
}
