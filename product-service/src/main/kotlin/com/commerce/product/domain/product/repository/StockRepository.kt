package com.commerce.product.domain.product.repository

import com.commerce.product.domain.product.entity.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import jakarta.persistence.LockModeType

interface StockRepository : JpaRepository<Stock, Long> {

    fun findByProductId(productId: Long): Stock?

    // 낙관적 락과 함께 사용 (Redisson 분산 락이 1차 보호)
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT s FROM Stock s WHERE s.productId = :productId")
    fun findByProductIdWithLock(productId: Long): Stock?
}
