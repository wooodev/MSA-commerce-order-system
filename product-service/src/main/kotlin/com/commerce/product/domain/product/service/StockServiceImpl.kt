package com.commerce.product.domain.product.service

import com.commerce.product.common.exception.InsufficientStockException
import com.commerce.product.common.exception.ProductNotFoundException
import com.commerce.product.common.exception.StockLockAcquisitionException
import com.commerce.product.domain.outbox.entity.OutboxEvent
import com.commerce.product.domain.outbox.repository.OutboxEventRepository
import com.commerce.product.domain.product.repository.StockRepository
import com.commerce.product.infrastructure.kafka.event.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
@Transactional
class StockServiceImpl(
    private val stockRepository: StockRepository,
    private val outboxEventRepository: OutboxEventRepository,
    private val redissonClient: RedissonClient,
    private val objectMapper: ObjectMapper
) : StockService {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 재고 차감 (Redisson 분산 락 적용).
     * 락 키: "lock:stock:{productId}", TTL 3초, 대기 1초
     *
     * 처리 흐름:
     *   1. 정렬된 순서로 모든 락 획득 (DeadLock 방지)
     *   2. 전체 재고 유효성 검증
     *   3. 일괄 차감 후 STOCK_DEDUCTED outbox 저장
     *   InsufficientStockException → STOCK_FAILED outbox 저장 (보상 트랜잭션 트리거)
     */
    override fun deductStock(orderId: Long, items: List<OrderItemPayload>) {
        // 멱등성 체크
        if (outboxEventRepository.existsByAggregateIdAndEventTypeIn(
                orderId.toString(), listOf("STOCK_DEDUCTED", "STOCK_FAILED")
            )
        ) {
            log.info("재고 차감 이미 처리됨 (멱등성): orderId=$orderId")
            return
        }

        val sortedItems = items.sortedBy { it.productId } // DeadLock 방지
        val acquiredLocks = mutableListOf<org.redisson.api.RLock>()

        try {
            // Step 1: 모든 락 획득
            sortedItems.forEach { item ->
                val lock = redissonClient.getLock("lock:stock:${item.productId}")
                if (!lock.tryLock(1, 3, TimeUnit.SECONDS)) {
                    throw StockLockAcquisitionException(item.productId)
                }
                acquiredLocks.add(lock)
                log.debug("재고 락 획득: productId=${item.productId}")
            }

            // Step 2: 전체 재고 유효성 검증 (차감 전 사전 확인)
            val stockMap = sortedItems.associate { item ->
                val stock = stockRepository.findByProductIdWithLock(item.productId)
                    ?: throw ProductNotFoundException(item.productId)
                if (stock.quantity < item.quantity) {
                    throw InsufficientStockException(item.productId, item.quantity, stock.quantity)
                }
                item.productId to stock
            }

            // Step 3: 일괄 차감
            val deductedItems = sortedItems.map { item ->
                val stock = stockMap[item.productId]
                    ?: throw IllegalStateException("재고 조회 실패 — 검증 통과 후 조회 불가: productId=${item.productId}")
                stock.deduct(item.quantity)
                stockRepository.save(stock)
                StockItemPayload(item.productId, item.quantity)
            }

            outboxEventRepository.save(
                OutboxEvent(
                    eventType = "STOCK_DEDUCTED",
                    aggregateId = orderId.toString(),
                    topic = KafkaTopics.STOCK_EVENTS,
                    payload = objectMapper.writeValueAsString(
                        StockDeductedPayload(orderId = orderId, items = deductedItems)
                    )
                )
            )
            log.info("재고 차감 완료: orderId=$orderId, items=${deductedItems.size}건")

        } catch (e: InsufficientStockException) {
            log.warn("재고 부족 — STOCK_FAILED 발행: orderId=$orderId, productId=${e.productId}")
            saveStockFailedEvent(orderId, e.productId, e.message ?: "재고 부족")

        } catch (e: ProductNotFoundException) {
            log.warn("상품 없음 — STOCK_FAILED 발행: orderId=$orderId, productId=${e.productId}")
            saveStockFailedEvent(orderId, e.productId, "상품을 찾을 수 없습니다")

        } finally {
            acquiredLocks.forEach { lock ->
                if (lock.isHeldByCurrentThread) {
                    lock.unlock()
                    log.debug("재고 락 해제: ${lock.name}")
                }
            }
        }
    }

    private fun saveStockFailedEvent(orderId: Long, productId: Long, reason: String) {
        outboxEventRepository.save(
            OutboxEvent(
                eventType = "STOCK_FAILED",
                aggregateId = orderId.toString(),
                topic = KafkaTopics.STOCK_EVENTS,
                payload = objectMapper.writeValueAsString(
                    StockFailedPayload(orderId = orderId, productId = productId, reason = reason)
                )
            )
        )
    }
}
