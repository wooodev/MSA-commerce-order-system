package com.commerce.product.domain.outbox.repository

import com.commerce.product.domain.outbox.entity.OutboxEvent
import org.springframework.data.jpa.repository.JpaRepository

interface OutboxEventRepository : JpaRepository<OutboxEvent, Long> {
    fun findTop100ByPublishedFalseOrderByCreatedAtAsc(): List<OutboxEvent>
    fun existsByAggregateIdAndEventTypeIn(aggregateId: String, eventTypes: List<String>): Boolean
}
