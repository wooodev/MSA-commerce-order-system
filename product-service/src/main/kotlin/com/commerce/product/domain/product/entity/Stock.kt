package com.commerce.product.domain.product.entity

import com.commerce.product.common.exception.InsufficientStockException
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "stocks",
    indexes = [Index(name = "idx_stocks_product_id", columnList = "product_id")]
)
class Stock(
    @Column(nullable = false, unique = true)
    val productId: Long,

    @Column(nullable = false)
    var quantity: Int = 0
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    // 낙관적 락 — Redisson 분산 락의 보조 수단
    @Version
    var version: Long = 0

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    // ───────── 도메인 메서드 ─────────

    /**
     * 재고 차감.
     * 반드시 Redisson 분산 락("lock:stock:{productId}") 획득 후 호출해야 함.
     */
    fun deduct(amount: Int) {
        if (quantity < amount) {
            throw InsufficientStockException(productId, amount, quantity)
        }
        quantity -= amount
        updatedAt = LocalDateTime.now()
    }

    fun restock(amount: Int) {
        require(amount > 0) { "재입고 수량은 양수여야 합니다" }
        quantity += amount
        updatedAt = LocalDateTime.now()
    }
}
