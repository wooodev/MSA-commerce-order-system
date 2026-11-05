package com.commerce.payment.domain.outbox.repository

import com.commerce.payment.domain.outbox.entity.OutboxEvent
import org.springframework.data.jpa.repository.JpaRepository

interface OutboxEventRepository : JpaRepository<OutboxEvent, Long> {
    fun findTop100ByPublishedFalseOrderByCreatedAtAsc(): List<OutboxEvent>
}
