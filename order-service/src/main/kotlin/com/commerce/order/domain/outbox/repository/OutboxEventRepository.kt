package com.commerce.order.domain.outbox.repository

import com.commerce.order.domain.outbox.entity.OutboxEvent
import org.springframework.data.jpa.repository.JpaRepository

interface OutboxEventRepository : JpaRepository<OutboxEvent, Long> {

    // 배치 크기 제한으로 스케줄러 부하 제어
    fun findTop100ByPublishedFalseOrderByCreatedAtAsc(): List<OutboxEvent>
}
