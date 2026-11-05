package com.commerce.payment.domain.payment.repository

import com.commerce.payment.domain.payment.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository : JpaRepository<Payment, Long> {
    fun findByOrderId(orderId: Long): Payment?
    fun existsByOrderId(orderId: Long): Boolean
    fun findAllByUserId(userId: Long): List<Payment>
}
