package com.commerce.payment.domain.payment.entity

import com.commerce.payment.common.BaseEntity
import com.commerce.payment.domain.payment.enums.PaymentStatus
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(
    name = "payments",
    indexes = [
        Index(name = "idx_payments_order_id", columnList = "order_id"),
        Index(name = "idx_payments_user_id", columnList = "user_id")
    ]
)
class Payment(
    @Column(nullable = false, unique = true)
    val orderId: Long,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    var status: PaymentStatus = PaymentStatus.PENDING
        private set

    // ───────── 도메인 메서드 ─────────

    fun complete() {
        check(status == PaymentStatus.PENDING) { "결제 완료 처리 불가 상태: $status" }
        status = PaymentStatus.COMPLETED
    }

    fun fail() {
        check(status == PaymentStatus.PENDING) { "결제 실패 처리 불가 상태: $status" }
        status = PaymentStatus.FAILED
    }

    fun refund() {
        check(status == PaymentStatus.COMPLETED) { "환불 처리 불가 상태: $status" }
        status = PaymentStatus.REFUNDED
    }
}
