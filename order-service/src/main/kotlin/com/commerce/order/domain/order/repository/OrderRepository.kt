package com.commerce.order.domain.order.repository

import com.commerce.order.domain.order.entity.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OrderRepository : JpaRepository<Order, Long> {

    fun findByIdAndUserId(id: Long, userId: Long): Order?

    fun findAllByUserId(userId: Long, pageable: Pageable): Page<Order>

    // N+1 방지: items fetch join
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id AND o.userId = :userId")
    fun findByIdAndUserIdWithItems(id: Long, userId: Long): Order?
}
