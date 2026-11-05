package com.commerce.product.domain.product.service

import com.commerce.product.infrastructure.kafka.event.OrderItemPayload

interface StockService {
    /**
     * 분산 락(Redisson) 기반 재고 차감.
     * PAYMENT_COMPLETED 이벤트 수신 시 호출.
     * 성공 → STOCK_DEDUCTED outbox 저장
     * 실패 → STOCK_FAILED outbox 저장 (보상 트랜잭션 트리거)
     */
    fun deductStock(orderId: Long, items: List<OrderItemPayload>)
}
