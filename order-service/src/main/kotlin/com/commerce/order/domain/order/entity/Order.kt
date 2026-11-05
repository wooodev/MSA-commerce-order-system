package com.commerce.order.domain.order.entity

import com.commerce.order.common.BaseEntity
import com.commerce.order.common.exception.InvalidOrderStatusException
import com.commerce.order.domain.order.enums.OrderStatus
import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(
    name = "orders",
    indexes = [Index(name = "idx_orders_user_created", columnList = "user_id, created_at")]
)
@SQLRestriction("deleted_at IS NULL")
class Order(
    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false, precision = 19, scale = 2)
    val totalAmount: BigDecimal,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    var status: OrderStatus = OrderStatus.PENDING
        private set

    @OneToMany(
        mappedBy = "order",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val items: MutableList<OrderItem> = mutableListOf()

    @Column
    var deletedAt: LocalDateTime? = null
        private set

    // ───────── 도메인 메서드 ─────────

    fun addItem(item: OrderItem) {
        items.add(item)
    }

    fun completePayment() {
        status = OrderStatus.PAYMENT_COMPLETED
    }

    fun failPayment() {
        status = OrderStatus.PAYMENT_FAILED
        doCancel()
    }

    fun completeStockDeduction() {
        status = OrderStatus.COMPLETED
    }

    fun failStockDeduction() {
        status = OrderStatus.STOCK_FAILED
        doCancel()
    }

    fun cancel() {
        if (status in listOf(OrderStatus.COMPLETED, OrderStatus.CANCELLED)) {
            throw InvalidOrderStatusException("완료되거나 취소된 주문은 취소할 수 없습니다: $status")
        }
        doCancel()
    }

    private fun doCancel() {
        status = OrderStatus.CANCELLED
        deletedAt = LocalDateTime.now()
    }
}
